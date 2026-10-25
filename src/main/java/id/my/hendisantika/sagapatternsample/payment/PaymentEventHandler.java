package id.my.hendisantika.sagapatternsample.payment;

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
 * Payment Service's choreography participant.
 */
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final PaymentService paymentService;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    public void on(OrderSagaEvent.OrderCreated event) {
        try {
            paymentService.processPayment(event.payload());
            eventPublisher.publishEvent(new OrderSagaEvent.PaymentCompleted(event.payload()));
        } catch (SagaStepException ex) {
            eventPublisher.publishEvent(new OrderSagaEvent.PaymentFailed(event.payload(), ex.getMessage()));
        }
    }

    @Async
    @EventListener
    public void on(OrderSagaEvent.StockReservationFailed event) {
        refund(event, event.reason());
    }

    @Async
    @EventListener
    public void on(OrderSagaEvent.StockReleased event) {
        refund(event, event.reason());
    }

    private void refund(OrderSagaEvent event, String reason) {
        paymentService.refundPayment(event.payload().orderId());
        eventPublisher.publishEvent(new OrderSagaEvent.PaymentRefunded(event.payload(), reason));
    }
}
