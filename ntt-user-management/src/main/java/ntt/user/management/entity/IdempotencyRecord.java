package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Model for tracking task idempotency and preventing duplicates
 */
@Entity
@Table(name = "idempotency_records", indexes = {
        @Index(name = "idx_idempotency_key", columnList = "idempotency_key"),
        @Index(name = "idx_task_id", columnList = "task_id"),
        @Index(name = "idx_task_name", columnList = "task_name"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "ix_idempotency_records_task_name_status", columnList = "task_name, status"),
        @Index(name = "ix_idempotency_records_created_at", columnList = "created_at"),
        @Index(name = "ix_idempotency_records_executed_at", columnList = "executed_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "idempotency_key", length = 64, unique = true, nullable = false)
    private String idempotencyKey;

    @Column(name = "task_id", length = 255, nullable = false)
    private String taskId;

    @Column(name = "task_name", length = 255, nullable = false)
    private String taskName;

    @Column(length = 50, nullable = false)
    private String status; // running, completed, failed, duplicate

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "input_data", columnDefinition = "JSON")
    private Map<String, Object> inputData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_data", columnDefinition = "JSON")
    private Map<String, Object> resultData;

    @Lob
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Override
    public String toString() {
        return String.format("<IdempotencyRecord(id=%d, task_name='%s', status='%s')>", id, taskName, status);
    }
}