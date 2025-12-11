package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_knowledge_bases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectKnowledgeBase extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "knowledge_base_id", nullable = false)
    private Integer knowledgeBaseId;

    @Column(name = "kb_type", length = 50, nullable = false)
    private String kbType;

    @Column(length = 255, nullable = false)
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_base_id", insertable = false, updatable = false)
    private KnowledgeBase knowledgeBase;
}