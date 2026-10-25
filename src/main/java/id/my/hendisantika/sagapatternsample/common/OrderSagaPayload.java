package id.my.hendisantika.sagapatternsample.common;

import java.math.BigDecimal;
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
 * The data every saga participant needs to perform (or undo) its local transaction.
 */
public record OrderSagaPayload(
        UUID orderId,
        String customerId,
        String productCode,
        int quantity,
        BigDecimal amount,
        String shippingCity
) {

    public static OrderSagaPayload newOrder(CreateOrderRequest request) {
        return new OrderSagaPayload(
                UUID.randomUUID(),
                request.customerId(),
                request.productCode(),
                request.quantity(),
                request.amount(),
                request.shippingCity()
        );
    }
}
