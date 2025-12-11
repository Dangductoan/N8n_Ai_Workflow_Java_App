package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "workflow_executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowExecution extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 36, unique = true, nullable = false)
    private String uuid;

    @Column(name = "workflow_id", nullable = false)
    private Integer workflowId;

    @Column(name = "execution_id", length = 36, unique = true, nullable = false)
    private String executionId;

    @Builder.Default
    @Column(length = 50, nullable = false)
    private String status = "running";

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "input_data", columnDefinition = "JSON")
    private Map<String, Object> inputData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "output_data", columnDefinition = "JSON")
    private Map<String, Object> outputData;

    @Lob
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "execution_log", columnDefinition = "JSON")
    private Map<String, Object> executionLog;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", insertable = false, updatable = false)
    private Workflow workflow;

    @PrePersist
    protected void onCreateExecution() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
    }
}
