package id.my.hendisantika.sagapatternsample.saga.orchestration;

import id.my.hendisantika.sagapatternsample.common.CreateOrderRequest;
import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.common.ResourceNotFoundException;
import id.my.hendisantika.sagapatternsample.order.OrderService;
import id.my.hendisantika.sagapatternsample.saga.orchestration.step.CreateOrderStep;
import id.my.hendisantika.sagapatternsample.saga.orchestration.step.CreateShipmentStep;
import id.my.hendisantika.sagapatternsample.saga.orchestration.step.ProcessPaymentStep;
import id.my.hendisantika.sagapatternsample.saga.orchestration.step.ReserveStockStep;
import id.my.hendisantika.sagapatternsample.saga.orchestration.step.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
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
 * Central orchestrator: runs each local transaction in order and, when one fails,
 * runs the compensating transactions of the completed steps in reverse order.
 * <p>
 * It is deliberately not {@code @Transactional}: there is no transaction spanning the services.
 */
@Slf4j
@Service
public class OrderSagaOrchestrator {

    private static final int MAX_COMPENSATION_ATTEMPTS = 3;

    private final List<SagaStep> steps;
    private final OrderService orderService;
    private final SagaInstanceRepository sagaRepository;

    public OrderSagaOrchestrator(CreateOrderStep createOrderStep,
                                 ProcessPaymentStep processPaymentStep,
                                 ReserveStockStep reserveStockStep,
                                 CreateShipmentStep createShipmentStep,
                                 OrderService orderService,
                                 SagaInstanceRepository sagaRepository) {
        this.steps = List.of(createOrderStep, processPaymentStep, reserveStockStep, createShipmentStep);
        this.orderService = orderService;
        this.sagaRepository = sagaRepository;
    }

    public SagaInstance placeOrder(CreateOrderRequest request) {
        OrderSagaPayload payload = OrderSagaPayload.newOrder(request);
        SagaInstance saga = sagaRepository.save(SagaInstance.start(payload.orderId()));
        log.info("[SAGA] Saga {} started for order {}", saga.getId(), payload.orderId());

        Deque<SagaStep> completedSteps = new ArrayDeque<>();
        for (SagaStep step : steps) {
            try {
                step.execute(payload);
                saga.record(step.name(), StepAction.EXECUTE, StepOutcome.SUCCESS, null);
                saga = sagaRepository.save(saga);
                completedSteps.push(step);
            } catch (RuntimeException ex) {
                log.warn("[SAGA] Step {} failed for order {}: {}", step.name(), payload.orderId(), ex.getMessage());
                saga.record(step.name(), StepAction.EXECUTE, StepOutcome.FAILURE, ex.getMessage());
                saga.startCompensation(step.name(), ex.getMessage());
                saga = sagaRepository.save(saga);
                return compensate(saga, completedSteps, payload, ex.getMessage());
            }
        }

        orderService.completeOrder(payload.orderId());
        saga.finish(SagaStatus.COMPLETED);
        log.info("[SAGA] Saga {} completed", saga.getId());
        return sagaRepository.save(saga);
    }

    public SagaInstance getSaga(UUID sagaId) {
        return sagaRepository.findById(sagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Saga " + sagaId + " not found"));
    }

    public List<SagaInstance> getSagas() {
        return sagaRepository.findAll();
    }

    private SagaInstance compensate(SagaInstance saga, Deque<SagaStep> completedSteps,
                                    OrderSagaPayload payload, String reason) {
        boolean allCompensated = true;
        // completedSteps is a stack, so this iterates in reverse order of execution
        for (SagaStep step : completedSteps) {
            allCompensated &= compensateWithRetry(saga, step, payload, reason);
            saga = sagaRepository.save(saga);
        }
        saga.finish(allCompensated ? SagaStatus.COMPENSATED : SagaStatus.FAILED);
        log.info("[SAGA] Saga {} finished with status {}", saga.getId(), saga.getStatus());
        return sagaRepository.save(saga);
    }

    private boolean compensateWithRetry(SagaInstance saga, SagaStep step, OrderSagaPayload payload, String reason) {
        for (int attempt = 1; attempt <= MAX_COMPENSATION_ATTEMPTS; attempt++) {
            try {
                step.compensate(payload, reason);
                saga.record(step.name(), StepAction.COMPENSATE, StepOutcome.SUCCESS, null);
                return true;
            } catch (RuntimeException ex) {
                log.warn("[SAGA] Compensation of {} failed (attempt {}/{}): {}",
                        step.name(), attempt, MAX_COMPENSATION_ATTEMPTS, ex.getMessage());
                if (attempt == MAX_COMPENSATION_ATTEMPTS) {
                    saga.record(step.name(), StepAction.COMPENSATE, StepOutcome.FAILURE, ex.getMessage());
                }
            }
        }
        return false;
    }
}
