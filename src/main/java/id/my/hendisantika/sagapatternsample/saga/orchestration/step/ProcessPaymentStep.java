package id.my.hendisantika.sagapatternsample.saga.orchestration.step;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.payment.PaymentService;
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
public class ProcessPaymentStep implements SagaStep {

    private final PaymentService paymentService;

    @Override
    public String name() {
        return "PROCESS_PAYMENT";
    }

    @Override
    public void execute(OrderSagaPayload payload) {
        paymentService.processPayment(payload);
    }

    @Override
    public void compensate(OrderSagaPayload payload, String reason) {
        paymentService.refundPayment(payload.orderId());
    }
}
