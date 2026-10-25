package id.my.hendisantika.sagapatternsample.saga.choreography;

import id.my.hendisantika.sagapatternsample.common.CreateOrderRequest;
import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.common.SagaType;
import id.my.hendisantika.sagapatternsample.order.Order;
import id.my.hendisantika.sagapatternsample.order.OrderService;
import id.my.hendisantika.sagapatternsample.saga.choreography.event.OrderSagaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
 * Starts a choreography-based saga: creates the order and announces it. From here on,
 * each service reacts to events on its own; nobody coordinates the flow.
 */
@Service
@RequiredArgsConstructor
public class ChoreographyOrderService {

    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    public Order placeOrder(CreateOrderRequest request) {
        OrderSagaPayload payload = OrderSagaPayload.newOrder(request);
        // the local transaction commits before the event is published
        Order order = orderService.createOrder(payload, SagaType.CHOREOGRAPHY);
        eventPublisher.publishEvent(new OrderSagaEvent.OrderCreated(payload));
        return order;
    }
}
