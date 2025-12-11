package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity
@Table(name = "workflow_connections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowConnection extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 36, unique = true, nullable = false)
    private String uuid;

    @Column(name = "workflow_id", nullable = false)
    private Integer workflowId;

    @Column(name = "source_node_id", nullable = false)
    private Integer sourceNodeId;

    @Column(name = "target_node_id", nullable = false)
    private Integer targetNodeId;

    @Builder.Default
    @Column(name = "connection_type", length = 50, nullable = false)
    private String connectionType = "success";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "condition_config", columnDefinition = "JSON")
    private Map<String, Object> conditionConfig;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", insertable = false, updatable = false)
    private Workflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", insertable = false, updatable = false)
    private WorkflowNode sourceNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", insertable = false, updatable = false)
    private WorkflowNode targetNode;
}
