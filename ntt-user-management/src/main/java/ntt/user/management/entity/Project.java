package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 36, unique = true, nullable = false)
    private String uuid;

    @Column(length = 255, nullable = false)
    private String name;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "company_uuid", length = 36)
    private String companyUuid;

    @Column(name = "company_id")
    private Integer companyId;

    @Builder.Default
    @Column(name = "project_type", length = 50, nullable = false)
    private String projectType = "software";

    @Builder.Default
    @Column(length = 50, nullable = false)
    private String status = "active";

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Builder.Default
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectKnowledgeBase> knowledgeBases = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnowledgeBase> directKnowledgeBases = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Workflow> workflows = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTask> projectTasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectUserLink> userLinks = new ArrayList<>();

    // n8n Integration
    @OneToOne(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private N8nProject n8nProject;

    @Builder.Default
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<N8nWorkflow> n8nWorkflows = new ArrayList<>();
}
