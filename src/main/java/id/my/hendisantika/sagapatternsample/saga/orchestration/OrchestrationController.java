package id.my.hendisantika.sagapatternsample.saga.orchestration;

import id.my.hendisantika.sagapatternsample.common.CreateOrderRequest;
import id.my.hendisantika.sagapatternsample.order.Order;
import id.my.hendisantika.sagapatternsample.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping("/api/orchestration")
@RequiredArgsConstructor
public class OrchestrationController {

    private final OrderSagaOrchestrator orchestrator;
    private final OrderService orderService;

    /**
     * Runs the whole saga synchronously and returns its final state.
     */
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public SagaResponse placeOrder(@Valid @RequestBody CreateOrderRequest request) {
        return toResponse(orchestrator.placeOrder(request));
    }

    @GetMapping("/sagas")
    public List<SagaResponse> getSagas() {
        return orchestrator.getSagas().stream().map(this::toResponse).toList();
    }

    @GetMapping("/sagas/{id}")
    public SagaResponse getSaga(@PathVariable UUID id) {
        return toResponse(orchestrator.getSaga(id));
    }

    private SagaResponse toResponse(SagaInstance saga) {
        // the order row is missing only when the very first step (create order) failed
        var orderStatus = orderService.findOrder(saga.getOrderId())
                .map(Order::getStatus)
                .orElse(null);
        return SagaResponse.from(saga, orderStatus);
    }
}
