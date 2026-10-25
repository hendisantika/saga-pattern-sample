package id.my.hendisantika.sagapatternsample.saga.orchestration;

import org.springframework.data.jpa.repository.JpaRepository;

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
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, UUID> {
}
