package id.my.hendisantika.sagapatternsample.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

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
 * Choreography event handlers run asynchronously, like consumers of a message broker would.
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties(SagaProperties.class)
public class AsyncConfig {
}
