package id.my.hendisantika.sagapatternsample.saga.orchestration;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SagaStepRecord {

    private String stepName;

    @Enumerated(EnumType.STRING)
    private StepAction action;

    @Enumerated(EnumType.STRING)
    private StepOutcome outcome;

    private String message;

    private Instant occurredAt;
}
