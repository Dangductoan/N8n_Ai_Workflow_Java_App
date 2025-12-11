package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "knowledge_base_user_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeBaseUserLink {

    @EmbeddedId
    private KnowledgeBaseUserLinkId id;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permissions", nullable = false, columnDefinition = "JSON")
    private List<String> permissions = new ArrayList<>();

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("knowledgeBaseId")
    @JoinColumn(name = "knowledge_base_id")
    private KnowledgeBase knowledgeBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}
