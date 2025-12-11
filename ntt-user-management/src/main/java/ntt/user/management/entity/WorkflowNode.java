package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "workflow_nodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowNode extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 36, unique = true, nullable = false)
    private String uuid;

    @Column(name = "workflow_id", nullable = false)
    private Integer workflowId;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(name = "node_type", length = 50, nullable = false)
    private String nodeType;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "position_x")
    private Integer positionX;

    @Column(name = "position_y")
    private Integer positionY;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> config;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", insertable = false, updatable = false)
    private Workflow workflow;

    @Builder.Default
    @OneToMany(mappedBy = "sourceNode", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowConnection> sourceConnections = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "targetNode", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowConnection> targetConnections = new ArrayList<>();
}
