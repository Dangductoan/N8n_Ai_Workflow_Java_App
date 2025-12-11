package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_api_keys" , indexes = { @Index(name = "idx_chatbot_api_key", columnList = "`key`")}
        )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotAPIKey extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "`key`", length = 128, unique = true, nullable = false)
    private String key;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false)
    private AccessLevel accessLevel = AccessLevel.PRIVATE;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "company_uuid", length = 36)
    private String companyUuid;

    @Column(name = "company_id")
    private Integer companyId;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;
}