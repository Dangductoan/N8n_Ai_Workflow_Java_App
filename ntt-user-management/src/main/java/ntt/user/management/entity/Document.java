package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents", uniqueConstraints = {
        @UniqueConstraint(name = "uq_kb_file_name", columnNames = {"knowledge_base_id", "file_name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_path", length = 255, nullable = false)
    private String filePath;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @Column(name = "file_hash", length = 64)
    @org.hibernate.annotations.Index(name = "idx_file_hash")
    private String fileHash;

    @Column(name = "knowledge_base_id", nullable = false)
    private Integer knowledgeBaseId;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_base_id", insertable = false, updatable = false)
    private KnowledgeBase knowledgeBase;

    @Builder.Default
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    private List<ProcessingTask> processingTasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentChunk> chunks = new ArrayList<>();
}
