package id.my.hendisantika.sagapatternsample.payment;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.common.SagaStepException;
import id.my.hendisantika.sagapatternsample.config.SagaProperties;
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
 * Payment Service. Every public method is one local transaction against the payment database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SagaProperties sagaProperties;

    @Transactional
    public Payment processPayment(OrderSagaPayload payload) {
        // idempotent: a redelivered request must not charge the customer twice
        var existing = paymentRepository.findByOrderId(payload.orderId());
        if (existing.isPresent()) {
            return existing.get();
        }

        if (payload.amount().compareTo(sagaProperties.payment().maxAmount()) > 0) {
            throw new SagaStepException("Payment declined: amount %s exceeds limit %s"
                    .formatted(payload.amount(), sagaProperties.payment().maxAmount()));
        }

        Payment payment = new Payment();
        payment.setOrderId(payload.orderId());
        payment.setCustomerId(payload.customerId());
        payment.setAmount(payload.amount());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(payment.getCreatedAt());
        log.info("[PAYMENT] Charged {} for order {}", payload.amount(), payload.orderId());
        return paymentRepository.save(payment);
    }

    /**
     * Compensating action for {@link #processPayment}.
     */
    @Transactional
    public void refundPayment(UUID orderId) {
        paymentRepository.findByOrderId(orderId)
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.REFUNDED);
                    payment.setUpdatedAt(Instant.now());
                    log.info("[PAYMENT] Refunded {} for order {}", payment.getAmount(), orderId);
                });
    }

    @Transactional(readOnly = true)
    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }
}
