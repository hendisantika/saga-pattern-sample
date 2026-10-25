package id.my.hendisantika.sagapatternsample.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.List;

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
 * Business rules used to simulate failures in the saga participants.
 */
@ConfigurationProperties(prefix = "saga")
public record SagaProperties(Payment payment, Shipping shipping) {

    public record Payment(BigDecimal maxAmount) {
    }

    public record Shipping(List<String> unserviceableCities) {
    }
}
