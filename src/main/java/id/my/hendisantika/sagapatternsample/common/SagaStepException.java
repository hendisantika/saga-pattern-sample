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
/**
 * Thrown when a participant rejects its local transaction for a business reason
 * (payment declined, insufficient stock, unserviceable address, ...).
 */
public class SagaStepException extends RuntimeException {

    public SagaStepException(String message) {
        super(message);
    }
}
