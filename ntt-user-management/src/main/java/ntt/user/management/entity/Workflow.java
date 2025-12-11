package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "workflows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workflow extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 36, unique = true, nullable = false)
    private String uuid;

    @Column(length = 255, nullable = false)
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "workflow_type", length = 50, nullable = false)
    private String workflowType = "custom";

    @Builder.Default
    @Column(length = 50, nullable = false)
    private String status = "draft";

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "task_id")
    private Integer taskId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> config;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private ProjectTask task;

    @Builder.Default
    @OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowNode> nodes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowConnection> connections = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowExecution> executions = new ArrayList<>();
}
