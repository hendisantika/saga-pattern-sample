package id.my.hendisantika.sagapatternsample.shipping;

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
 * Shipping Service's choreography participant.
 */
@Component
@RequiredArgsConstructor
public class ShippingEventHandler {

    private final ShippingService shippingService;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    public void on(OrderSagaEvent.StockReserved event) {
        try {
            shippingService.createShipment(event.payload());
            eventPublisher.publishEvent(new OrderSagaEvent.ShipmentCreated(event.payload()));
        } catch (SagaStepException ex) {
            eventPublisher.publishEvent(new OrderSagaEvent.ShipmentFailed(event.payload(), ex.getMessage()));
        }
    }
}
