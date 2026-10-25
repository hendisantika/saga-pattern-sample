package id.my.hendisantika.sagapatternsample.saga.choreography;

import id.my.hendisantika.sagapatternsample.common.CreateOrderRequest;
import id.my.hendisantika.sagapatternsample.order.Order;
import id.my.hendisantika.sagapatternsample.order.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

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
@RequestMapping("/api/choreography")
@RequiredArgsConstructor
public class ChoreographyController {

    private final ChoreographyOrderService choreographyOrderService;

    /**
     * Returns 202 Accepted with the PENDING order: the saga continues asynchronously
     * (eventual consistency), so poll the Location header for the final status.
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = choreographyOrderService.placeOrder(request);
        return ResponseEntity.accepted()
                .location(URI.create("/api/orders/" + order.getId()))
                .body(OrderResponse.from(order));
    }
}
