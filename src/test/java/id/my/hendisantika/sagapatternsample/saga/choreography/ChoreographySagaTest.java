package id.my.hendisantika.sagapatternsample.saga.choreography;

import id.my.hendisantika.sagapatternsample.common.CreateOrderRequest;
import id.my.hendisantika.sagapatternsample.inventory.ProductRepository;
import id.my.hendisantika.sagapatternsample.inventory.ReservationStatus;
import id.my.hendisantika.sagapatternsample.inventory.StockReservationRepository;
import id.my.hendisantika.sagapatternsample.order.Order;
import id.my.hendisantika.sagapatternsample.order.OrderRepository;
import id.my.hendisantika.sagapatternsample.order.OrderStatus;
import id.my.hendisantika.sagapatternsample.payment.PaymentRepository;
import id.my.hendisantika.sagapatternsample.payment.PaymentStatus;
import id.my.hendisantika.sagapatternsample.shipping.ShipmentRepository;
import id.my.hendisantika.sagapatternsample.shipping.ShipmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

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
@SpringBootTest
class ChoreographySagaTest {

    @Autowired
    private ChoreographyOrderService choreographyOrderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockReservationRepository reservationRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Test
    void completesOrderWhenAllServicesSucceed() {
        Order order = choreographyOrderService.placeOrder(request("PHONE-001", 1, "800.00", "Bandung"));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        awaitOrderStatus(order.getId(), OrderStatus.COMPLETED);
        assertThat(paymentRepository.findByOrderId(order.getId()).orElseThrow().getStatus())
                .isEqualTo(PaymentStatus.COMPLETED);
        assertThat(reservationRepository.findByOrderId(order.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.RESERVED);
        assertThat(shipmentRepository.findByOrderId(order.getId()).orElseThrow().getStatus())
                .isEqualTo(ShipmentStatus.CREATED);
    }

    @Test
    void cancelsOrderWhenPaymentIsDeclined() {
        Order order = choreographyOrderService.placeOrder(request("PHONE-001", 1, "7500.00", "Bandung"));

        Order cancelled = awaitOrderStatus(order.getId(), OrderStatus.CANCELLED);
        assertThat(cancelled.getFailureReason()).contains("Payment declined");
        assertThat(paymentRepository.findByOrderId(order.getId())).isEmpty();
    }

    @Test
    void refundsPaymentWhenStockReservationFails() {
        Order order = choreographyOrderService.placeOrder(request("CONSOLE-001", 1, "500.00", "Jakarta"));

        Order cancelled = awaitOrderStatus(order.getId(), OrderStatus.CANCELLED);
        assertThat(cancelled.getFailureReason()).contains("Insufficient stock");
        assertThat(paymentRepository.findByOrderId(order.getId()).orElseThrow().getStatus())
                .isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    void releasesStockAndRefundsPaymentWhenShippingFails() {
        int stockBefore = productRepository.findById("MOUSE-001").orElseThrow().getAvailableStock();

        Order order = choreographyOrderService.placeOrder(request("MOUSE-001", 3, "75.00", "Gotham"));

        Order cancelled = awaitOrderStatus(order.getId(), OrderStatus.CANCELLED);
        assertThat(cancelled.getFailureReason()).contains("Shipping to Gotham");
        assertThat(reservationRepository.findByOrderId(order.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.RELEASED);
        assertThat(paymentRepository.findByOrderId(order.getId()).orElseThrow().getStatus())
                .isEqualTo(PaymentStatus.REFUNDED);
        assertThat(productRepository.findById("MOUSE-001").orElseThrow().getAvailableStock())
                .isEqualTo(stockBefore);
    }

    private Order awaitOrderStatus(UUID orderId, OrderStatus expected) {
        await().atMost(Duration.ofSeconds(5))
                .until(() -> orderRepository.findById(orderId).orElseThrow().getStatus() == expected);
        return orderRepository.findById(orderId).orElseThrow();
    }

    private static CreateOrderRequest request(String productCode, int quantity, String amount, String city) {
        return new CreateOrderRequest("CUST-002", productCode, quantity, new BigDecimal(amount), city);
    }
}
