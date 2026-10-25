package id.my.hendisantika.sagapatternsample.order;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.common.SagaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Entity
@Table(name = "orders", schema = "order_service")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String productCode;

    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String shippingCity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaType sagaType;

    private String failureReason;

    private Instant createdAt;

    private Instant updatedAt;

    public static Order pending(OrderSagaPayload payload, SagaType sagaType) {
        Order order = new Order();
        order.setId(payload.orderId());
        order.setCustomerId(payload.customerId());
        order.setProductCode(payload.productCode());
        order.setQuantity(payload.quantity());
        order.setAmount(payload.amount());
        order.setShippingCity(payload.shippingCity());
        order.setStatus(OrderStatus.PENDING);
        order.setSagaType(sagaType);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(order.getCreatedAt());
        return order;
    }
}
