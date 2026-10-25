package id.my.hendisantika.sagapatternsample.saga.orchestration;

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
public enum SagaStatus {
    STARTED,
    COMPENSATING,
    /**
     * All steps succeeded.
     */
    COMPLETED,
    /**
     * A step failed and every completed step was undone.
     */
    COMPENSATED,
    /**
     * A compensating transaction kept failing; needs manual intervention.
     */
    FAILED
}
