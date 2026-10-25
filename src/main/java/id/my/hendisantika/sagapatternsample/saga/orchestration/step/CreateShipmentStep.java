package id.my.hendisantika.sagapatternsample.saga.orchestration.step;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.shipping.ShippingService;
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
public class CreateShipmentStep implements SagaStep {

    private final ShippingService shippingService;

    @Override
    public String name() {
        return "CREATE_SHIPMENT";
    }

    @Override
    public void execute(OrderSagaPayload payload) {
        shippingService.createShipment(payload);
    }

    @Override
    public void compensate(OrderSagaPayload payload, String reason) {
        shippingService.cancelShipment(payload.orderId());
    }
}
