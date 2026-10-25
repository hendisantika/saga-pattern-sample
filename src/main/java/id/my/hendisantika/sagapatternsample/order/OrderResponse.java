package id.my.hendisantika.sagapatternsample.order;

import id.my.hendisantika.sagapatternsample.common.SagaType;

import java.math.BigDecimal;
import java.time.Instant;
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
public record OrderResponse(
        UUID id,
        String customerId,
        String productCode,
        int quantity,
        BigDecimal amount,
        String shippingCity,
        OrderStatus status,
        SagaType sagaType,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getProductCode(),
                order.getQuantity(),
                order.getAmount(),
                order.getShippingCity(),
                order.getStatus(),
                order.getSagaType(),
                order.getFailureReason(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
