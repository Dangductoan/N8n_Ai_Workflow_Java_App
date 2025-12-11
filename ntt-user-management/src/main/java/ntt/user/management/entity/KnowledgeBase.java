package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "knowledge_bases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeBase extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    @Column(name = "project_id")
    private Integer projectId;

    @Builder.Default
    @Column(length = 32, nullable = false)
    private String source = "local";

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @Builder.Default
    @OneToMany(mappedBy = "knowledgeBase", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "knowledgeBase", fetch = FetchType.LAZY)
    private List<ProcessingTask> processingTasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "knowledgeBase", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentChunk> chunks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "knowledgeBase", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentUpload> documentUploads = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "knowledgeBase", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnowledgeBaseUserLink> userLinks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "knowledgeBase", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CloudStorageLink> cloudStorageLinks = new ArrayList<>();
}