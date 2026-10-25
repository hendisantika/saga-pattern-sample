package id.my.hendisantika.sagapatternsample.saga.choreography.event;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;

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
 * Events exchanged between services in the choreography-based saga.
 * In production these would travel through a broker such as Kafka or RabbitMQ.
 * <pre>
 * Happy path : OrderCreated -> PaymentCompleted -> StockReserved -> ShipmentCreated -> (order completed)
 * Payment    : OrderCreated -> PaymentFailed -> (order cancelled)
 * Inventory  : ... -> StockReservationFailed -> PaymentRefunded -> (order cancelled)
 * Shipping   : ... -> ShipmentFailed -> StockReleased -> PaymentRefunded -> (order cancelled)
 * </pre>
 */
public sealed interface OrderSagaEvent {

    OrderSagaPayload payload();

    record OrderCreated(OrderSagaPayload payload) implements OrderSagaEvent {
    }

    record PaymentCompleted(OrderSagaPayload payload) implements OrderSagaEvent {
    }

    record PaymentFailed(OrderSagaPayload payload, String reason) implements OrderSagaEvent {
    }

    record StockReserved(OrderSagaPayload payload) implements OrderSagaEvent {
    }

    record StockReservationFailed(OrderSagaPayload payload, String reason) implements OrderSagaEvent {
    }

    record ShipmentCreated(OrderSagaPayload payload) implements OrderSagaEvent {
    }

    record ShipmentFailed(OrderSagaPayload payload, String reason) implements OrderSagaEvent {
    }

    record StockReleased(OrderSagaPayload payload, String reason) implements OrderSagaEvent {
    }

    record PaymentRefunded(OrderSagaPayload payload, String reason) implements OrderSagaEvent {
    }
}
