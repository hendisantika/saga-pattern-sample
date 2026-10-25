package id.my.hendisantika.sagapatternsample.saga.orchestration.step;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.common.SagaType;
import id.my.hendisantika.sagapatternsample.order.OrderService;
import lombok.RequiredArgsConstructor;
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
@Component
@RequiredArgsConstructor
public class CreateOrderStep implements SagaStep {

    private final OrderService orderService;

    @Override
    public String name() {
        return "CREATE_ORDER";
    }

    @Override
    public void execute(OrderSagaPayload payload) {
        orderService.createOrder(payload, SagaType.ORCHESTRATION);
    }

    @Override
    public void compensate(OrderSagaPayload payload, String reason) {
        orderService.cancelOrder(payload.orderId(), reason);
    }
}
