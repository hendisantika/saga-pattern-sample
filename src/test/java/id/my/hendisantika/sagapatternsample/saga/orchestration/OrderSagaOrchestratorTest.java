package id.my.hendisantika.sagapatternsample.saga.orchestration;

import id.my.hendisantika.sagapatternsample.common.CreateOrderRequest;
import id.my.hendisantika.sagapatternsample.inventory.ProductRepository;
import id.my.hendisantika.sagapatternsample.inventory.ReservationStatus;
import id.my.hendisantika.sagapatternsample.inventory.StockReservationRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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
class OrderSagaOrchestratorTest {

    @Autowired
    private OrderSagaOrchestrator orchestrator;

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
    void completesSagaWhenAllStepsSucceed() {
        int stockBefore = stockOf("LAPTOP-001");

        SagaInstance saga = orchestrator.placeOrder(request("LAPTOP-001", 1, "1500.00", "Jakarta"));

        assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPLETED);
        assertThat(saga.getHistory())
                .extracting(SagaStepRecord::getStepName, SagaStepRecord::getAction, SagaStepRecord::getOutcome)
                .containsExactly(
                        tuple("CREATE_ORDER", StepAction.EXECUTE, StepOutcome.SUCCESS),
                        tuple("PROCESS_PAYMENT", StepAction.EXECUTE, StepOutcome.SUCCESS),
                        tuple("RESERVE_STOCK", StepAction.EXECUTE, StepOutcome.SUCCESS),
                        tuple("CREATE_SHIPMENT", StepAction.EXECUTE, StepOutcome.SUCCESS));
        assertThat(orderRepository.findById(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(OrderStatus.COMPLETED);
        assertThat(paymentRepository.findByOrderId(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(PaymentStatus.COMPLETED);
        assertThat(reservationRepository.findByOrderId(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.RESERVED);
        assertThat(shipmentRepository.findByOrderId(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(ShipmentStatus.CREATED);
        assertThat(stockOf("LAPTOP-001")).isEqualTo(stockBefore - 1);
    }

    @Test
    void cancelsOrderWhenPaymentIsDeclined() {
        SagaInstance saga = orchestrator.placeOrder(request("PHONE-001", 1, "9000.00", "Bandung"));

        assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPENSATED);
        assertThat(saga.getFailedStep()).isEqualTo("PROCESS_PAYMENT");
        assertThat(saga.getFailureReason()).contains("Payment declined");
        assertThat(orderRepository.findById(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(OrderStatus.CANCELLED);
        assertThat(paymentRepository.findByOrderId(saga.getOrderId())).isEmpty();
    }

    @Test
    void refundsPaymentAndCancelsOrderWhenStockReservationFails() {
        SagaInstance saga = orchestrator.placeOrder(request("CONSOLE-001", 1, "500.00", "Surabaya"));

        assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPENSATED);
        assertThat(saga.getFailedStep()).isEqualTo("RESERVE_STOCK");
        assertThat(saga.getFailureReason()).contains("Insufficient stock");
        // compensations run in reverse order of execution
        assertThat(saga.getHistory())
                .extracting(SagaStepRecord::getStepName, SagaStepRecord::getAction, SagaStepRecord::getOutcome)
                .containsExactly(
                        tuple("CREATE_ORDER", StepAction.EXECUTE, StepOutcome.SUCCESS),
                        tuple("PROCESS_PAYMENT", StepAction.EXECUTE, StepOutcome.SUCCESS),
                        tuple("RESERVE_STOCK", StepAction.EXECUTE, StepOutcome.FAILURE),
                        tuple("PROCESS_PAYMENT", StepAction.COMPENSATE, StepOutcome.SUCCESS),
                        tuple("CREATE_ORDER", StepAction.COMPENSATE, StepOutcome.SUCCESS));
        assertThat(paymentRepository.findByOrderId(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(PaymentStatus.REFUNDED);
        assertThat(orderRepository.findById(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void releasesStockAndRefundsPaymentWhenShippingFails() {
        int stockBefore = stockOf("MOUSE-001");

        SagaInstance saga = orchestrator.placeOrder(request("MOUSE-001", 2, "50.00", "Atlantis"));

        assertThat(saga.getStatus()).isEqualTo(SagaStatus.COMPENSATED);
        assertThat(saga.getFailedStep()).isEqualTo("CREATE_SHIPMENT");
        assertThat(reservationRepository.findByOrderId(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.RELEASED);
        assertThat(paymentRepository.findByOrderId(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(PaymentStatus.REFUNDED);
        assertThat(orderRepository.findById(saga.getOrderId()).orElseThrow().getStatus())
                .isEqualTo(OrderStatus.CANCELLED);
        assertThat(shipmentRepository.findByOrderId(saga.getOrderId())).isEmpty();
        assertThat(stockOf("MOUSE-001")).isEqualTo(stockBefore);
    }

    private int stockOf(String productCode) {
        return productRepository.findById(productCode).orElseThrow().getAvailableStock();
    }

    private static CreateOrderRequest request(String productCode, int quantity, String amount, String city) {
        return new CreateOrderRequest("CUST-001", productCode, quantity, new BigDecimal(amount), city);
    }
}
