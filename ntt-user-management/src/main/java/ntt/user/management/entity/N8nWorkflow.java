package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Mapping table between system workflows and n8n workflows
 */
@Entity
@Table(name = "n8n_workflows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class N8nWorkflow extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "n8n_project_id", nullable = false)
    private Integer n8nProjectId;

    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "n8n_workflow_id", length = 255, unique = true, nullable = false)
    private String n8nWorkflowId;

    @Column(name = "n8n_workflow_name", length = 255, nullable = false)
    private String n8nWorkflowName;

    @Column(name = "n8n_workflow_version_id", length = 255)
    private String n8nWorkflowVersionId;

    @Builder.Default
    @Column(name = "n8n_workflow_active")
    private Boolean n8nWorkflowActive = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "n8n_workflow_data", columnDefinition = "JSON")
    private Map<String, Object> n8nWorkflowData;

    @Column(name = "web_hook_url", length = 500)
    private String webHookUrl;

    @Column(name = "web_hook_method", length = 10)
    private String webHookMethod;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n8n_project_id", insertable = false, updatable = false)
    private N8nProject n8nProject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private ProjectTask task;
}