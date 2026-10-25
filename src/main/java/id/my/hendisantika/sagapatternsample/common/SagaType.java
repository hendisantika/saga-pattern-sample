package id.my.hendisantika.sagapatternsample.common;

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
public enum SagaType {
    /**
     * A central orchestrator calls each service and drives compensation.
     */
    ORCHESTRATION,
    /**
     * Services react to each other's events without a central coordinator.
     */
    CHOREOGRAPHY
}
