package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping table between system projects and n8n projects
 */
@Entity
@Table(name = "n8n_projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class N8nProject extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "project_id", unique = true, nullable = false)
    private Integer projectId;

    @Column(name = "n8n_project_id", length = 255, unique = true, nullable = false)
    private String n8nProjectId;

    @Column(name = "n8n_project_name", length = 255, nullable = false)
    private String n8nProjectName;

    @Lob
    @Column(name = "n8n_project_description", columnDefinition = "TEXT")
    private String n8nProjectDescription;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @Builder.Default
    @OneToMany(mappedBy = "n8nProject", fetch = FetchType.LAZY)
    private List<N8nWorkflow> workflows = new ArrayList<>();
}
