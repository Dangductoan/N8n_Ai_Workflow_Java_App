package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity
@Table(name = "document_chunks", indexes = {
        @Index(name = "idx_kb_file_name", columnList = "kb_id, file_name"),
        @Index(name = "idx_hash", columnList = "hash")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentChunk extends TimestampMixin {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "kb_id", nullable = false)
    private Integer kbId;

    @Column(name = "document_id", nullable = false)
    private Integer documentId;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chunk_metadata", columnDefinition = "JSON")
    private Map<String, Object> chunkMetadata;

    @Column(length = 64, nullable = false)
    private String hash;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kb_id", insertable = false, updatable = false)
    private KnowledgeBase knowledgeBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private Document document;
}
