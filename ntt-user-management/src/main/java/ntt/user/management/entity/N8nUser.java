package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Mapping table between system users and n8n users
 */
@Entity
@Table(name = "n8n_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class N8nUser extends TimestampMixin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Integer userId;

    @Column(name = "n8n_user_id", length = 255, unique = true, nullable = false)
    private String n8nUserId;

    @Column(name = "n8n_email", length = 255, nullable = false)
    private String n8nEmail;

    @Column(name = "n8n_api_key", length = 255)
    private String n8nApiKey;

    @Builder.Default
    @Column(name = "n8n_role", length = 50, nullable = false)
    private String n8nRole = "global:member";

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}