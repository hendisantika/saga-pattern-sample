package id.my.hendisantika.sagapatternsample.saga.orchestration.step;

import id.my.hendisantika.sagapatternsample.common.OrderSagaPayload;
import id.my.hendisantika.sagapatternsample.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
@Component
@RequiredArgsConstructor
public class ReserveStockStep implements SagaStep {

    private final InventoryService inventoryService;

    @Override
    public String name() {
        return "RESERVE_STOCK";
    }

    @Override
    public void execute(OrderSagaPayload payload) {
        inventoryService.reserveStock(payload);
    }

    @Override
    public void compensate(OrderSagaPayload payload, String reason) {
        inventoryService.releaseStock(payload.orderId());
    }
}
