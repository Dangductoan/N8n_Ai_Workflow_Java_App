package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys" ,indexes = { @Index(name = "idx_api_key", columnList = "`key`") }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class APIKey extends TimestampMixin {

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
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}