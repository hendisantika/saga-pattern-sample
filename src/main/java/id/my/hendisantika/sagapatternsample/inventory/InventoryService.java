package id.my.hendisantika.sagapatternsample.inventory;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.common.SagaStepException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
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
 * Inventory Service. Every public method is one local transaction against the inventory database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final StockReservationRepository reservationRepository;

    @Transactional
    public StockReservation reserveStock(OrderSagaPayload payload) {
        // idempotent: a redelivered request must not reserve the stock twice
        var existing = reservationRepository.findByOrderId(payload.orderId());
        if (existing.isPresent()) {
            return existing.get();
        }

        Product product = productRepository.findById(payload.productCode())
                .orElseThrow(() -> new SagaStepException("Unknown product " + payload.productCode()));
        if (product.getAvailableStock() < payload.quantity()) {
            throw new SagaStepException("Insufficient stock for %s: requested %d, available %d"
                    .formatted(product.getCode(), payload.quantity(), product.getAvailableStock()));
        }
        product.setAvailableStock(product.getAvailableStock() - payload.quantity());

        StockReservation reservation = new StockReservation();
        reservation.setOrderId(payload.orderId());
        reservation.setProductCode(product.getCode());
        reservation.setQuantity(payload.quantity());
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setCreatedAt(Instant.now());
        reservation.setUpdatedAt(reservation.getCreatedAt());
        log.info("[INVENTORY] Reserved {} x {} for order {}", payload.quantity(), product.getCode(), payload.orderId());
        return reservationRepository.save(reservation);
    }

    /**
     * Compensating action for {@link #reserveStock}.
     */
    @Transactional
    public void releaseStock(UUID orderId) {
        reservationRepository.findByOrderId(orderId)
                .filter(reservation -> reservation.getStatus() == ReservationStatus.RESERVED)
                .ifPresent(reservation -> {
                    Product product = productRepository.findById(reservation.getProductCode()).orElseThrow();
                    product.setAvailableStock(product.getAvailableStock() + reservation.getQuantity());
                    reservation.setStatus(ReservationStatus.RELEASED);
                    reservation.setUpdatedAt(Instant.now());
                    log.info("[INVENTORY] Released {} x {} for order {}",
                            reservation.getQuantity(), product.getCode(), orderId);
                });
    }

    @Transactional(readOnly = true)
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<StockReservation> getReservations() {
        return reservationRepository.findAll();
    }
}
