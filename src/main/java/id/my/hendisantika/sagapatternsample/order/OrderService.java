package id.my.hendisantika.sagapatternsample.order;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.common.ResourceNotFoundException;
import id.my.hendisantika.sagapatternsample.common.SagaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
 * Order Service. Every public method is one local transaction against the order database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(OrderSagaPayload payload, SagaType sagaType) {
        Order order = orderRepository.save(Order.pending(payload, sagaType));
        log.info("[ORDER] Order {} created (PENDING) via {}", order.getId(), sagaType);
        return order;
    }

    @Transactional
    public void completeOrder(UUID orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }
        order.setStatus(OrderStatus.COMPLETED);
        order.setUpdatedAt(Instant.now());
        log.info("[ORDER] Order {} completed", orderId);
    }

    /**
     * Compensating action for {@link #createOrder}.
     */
    @Transactional
    public void cancelOrder(UUID orderId, String reason) {
        Order order = getOrder(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setFailureReason(reason);
        order.setUpdatedAt(Instant.now());
        log.info("[ORDER] Order {} cancelled: {}", orderId, reason);
    }

    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {
        return findOrder(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderId + " not found"));
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOrder(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }
}
