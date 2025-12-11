package ntt.user.management.entity;

import jakarta.persistence.*;

// ============================================
// Chat Entity
// ============================================

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "company_uuid", length = 36)
    private String companyUuid;

    @Column(name = "company_id")
    private Integer companyId;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chat_knowledge_bases",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "knowledge_base_id")
    )
    private List<KnowledgeBase> knowledgeBases = new ArrayList<>();
}