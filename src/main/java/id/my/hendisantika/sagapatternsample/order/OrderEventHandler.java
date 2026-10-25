package id.my.hendisantika.sagapatternsample.order;

import id.my.hendisantika.sagapatternsample.saga.choreography.event.OrderSagaEvent;
import lombok.RequiredArgsConstructor;
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
 * Order Service's choreography participant: closes the saga by completing or cancelling the order.
 */
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final OrderService orderService;

    @Async
    @EventListener
    public void on(OrderSagaEvent.ShipmentCreated event) {
        orderService.completeOrder(event.payload().orderId());
    }

    @Async
    @EventListener
    public void on(OrderSagaEvent.PaymentFailed event) {
        orderService.cancelOrder(event.payload().orderId(), event.reason());
    }

    @Async
    @EventListener
    public void on(OrderSagaEvent.PaymentRefunded event) {
        orderService.cancelOrder(event.payload().orderId(), event.reason());
    }
}
