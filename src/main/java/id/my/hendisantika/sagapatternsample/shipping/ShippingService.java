package id.my.hendisantika.sagapatternsample.shipping;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.common.SagaStepException;
import id.my.hendisantika.sagapatternsample.config.SagaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Project : saga-pattern-sample
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Link : s.id/hendisantika
 * Date: 25/10/26
 * Time: 13.21
 */
/**
 * Shipping Service. Every public method is one local transaction against the shipping database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final SagaProperties sagaProperties;

    @Transactional
    public Shipment createShipment(OrderSagaPayload payload) {
        // idempotent: a redelivered request must not create a second shipment
        var existing = shipmentRepository.findByOrderId(payload.orderId());
        if (existing.isPresent()) {
            return existing.get();
        }

        boolean unserviceable = sagaProperties.shipping().unserviceableCities().stream()
                .anyMatch(city -> city.equalsIgnoreCase(payload.shippingCity()));
        if (unserviceable) {
            throw new SagaStepException("Shipping to " + payload.shippingCity() + " is not available");
        }

        Shipment shipment = new Shipment();
        shipment.setOrderId(payload.orderId());
        shipment.setShippingCity(payload.shippingCity());
        shipment.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        shipment.setStatus(ShipmentStatus.CREATED);
        shipment.setCreatedAt(Instant.now());
        shipment.setUpdatedAt(shipment.getCreatedAt());
        log.info("[SHIPPING] Shipment {} created for order {}", shipment.getTrackingNumber(), payload.orderId());
        return shipmentRepository.save(shipment);
    }

    /**
     * Compensating action for {@link #createShipment}.
     */
    @Transactional
    public void cancelShipment(UUID orderId) {
        shipmentRepository.findByOrderId(orderId)
                .filter(shipment -> shipment.getStatus() == ShipmentStatus.CREATED)
                .ifPresent(shipment -> {
                    shipment.setStatus(ShipmentStatus.CANCELLED);
                    shipment.setUpdatedAt(Instant.now());
                    log.info("[SHIPPING] Shipment {} cancelled for order {}", shipment.getTrackingNumber(), orderId);
                });
    }

    @Transactional(readOnly = true)
    public List<Shipment> getShipments() {
        return shipmentRepository.findAll();
    }
}
