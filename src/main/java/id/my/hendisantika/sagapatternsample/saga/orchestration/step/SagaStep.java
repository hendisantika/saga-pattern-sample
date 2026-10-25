package id.my.hendisantika.sagapatternsample.saga.orchestration.step;

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
 * One step of the saga: a local transaction plus the compensating transaction that undoes it.
 */
public interface SagaStep {

    String name();

    void execute(OrderSagaPayload payload);

    void compensate(OrderSagaPayload payload, String reason);
}
