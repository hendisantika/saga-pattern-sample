package id.my.hendisantika.sagapatternsample.inventory;

import id.my.hendisantika.sagapatternsample.common.SagaStepException;
import id.my.hendisantika.sagapatternsample.saga.choreography.event.OrderSagaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
 * Inventory Service's choreography participant.
 */
@Component
@RequiredArgsConstructor
public class InventoryEventHandler {

    private final InventoryService inventoryService;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    public void on(OrderSagaEvent.PaymentCompleted event) {
        try {
            inventoryService.reserveStock(event.payload());
            eventPublisher.publishEvent(new OrderSagaEvent.StockReserved(event.payload()));
        } catch (SagaStepException ex) {
            eventPublisher.publishEvent(new OrderSagaEvent.StockReservationFailed(event.payload(), ex.getMessage()));
        }
    }

    @Async
    @EventListener
    public void on(OrderSagaEvent.ShipmentFailed event) {
        inventoryService.releaseStock(event.payload().orderId());
        eventPublisher.publishEvent(new OrderSagaEvent.StockReleased(event.payload(), event.reason()));
    }
}
