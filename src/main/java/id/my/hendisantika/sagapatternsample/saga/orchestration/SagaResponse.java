package id.my.hendisantika.sagapatternsample.saga.orchestration;

import id.my.hendisantika.sagapatternsample.order.OrderStatus;

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
public record SagaResponse(
        UUID sagaId,
        UUID orderId,
        OrderStatus orderStatus,
        SagaStatus sagaStatus,
        String failedStep,
        String failureReason,
        List<Step> history
) {

    public static SagaResponse from(SagaInstance saga, OrderStatus orderStatus) {
        return new SagaResponse(
                saga.getId(),
                saga.getOrderId(),
                orderStatus,
                saga.getStatus(),
                saga.getFailedStep(),
                saga.getFailureReason(),
                saga.getHistory().stream()
                        .map(r -> new Step(r.getStepName(), r.getAction(), r.getOutcome(), r.getMessage(), r.getOccurredAt()))
                        .toList()
        );
    }

    public record Step(String name, StepAction action, StepOutcome outcome, String message, Instant occurredAt) {
    }
}
