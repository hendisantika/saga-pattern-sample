package id.my.hendisantika.sagapatternsample.saga.orchestration;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
/**
 * Persistent state of one orchestrated saga, so its progress can be monitored and audited.
 */
@Entity
@Table(name = "saga_instances", schema = "saga_orchestrator")
@Getter
@NoArgsConstructor
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    private String failedStep;

    private String failureReason;

    private Instant createdAt;

    private Instant updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "saga_step_history", schema = "saga_orchestrator",
            joinColumns = @JoinColumn(name = "saga_id"))
    @OrderColumn(name = "position")
    private List<SagaStepRecord> history = new ArrayList<>();

    public static SagaInstance start(UUID orderId) {
        SagaInstance saga = new SagaInstance();
        saga.orderId = orderId;
        saga.status = SagaStatus.STARTED;
        saga.createdAt = Instant.now();
        saga.updatedAt = saga.createdAt;
        return saga;
    }

    public void record(String stepName, StepAction action, StepOutcome outcome, String message) {
        history.add(new SagaStepRecord(stepName, action, outcome, message, Instant.now()));
        updatedAt = Instant.now();
    }

    public void startCompensation(String failedStep, String failureReason) {
        this.status = SagaStatus.COMPENSATING;
        this.failedStep = failedStep;
        this.failureReason = failureReason;
        this.updatedAt = Instant.now();
    }

    public void finish(SagaStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
