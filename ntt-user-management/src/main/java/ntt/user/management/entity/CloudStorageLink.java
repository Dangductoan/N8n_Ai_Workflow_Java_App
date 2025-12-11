package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cloud_storage_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloudStorageLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "knowledge_base_id")
    private Integer knowledgeBaseId;

    @Column(length = 50, nullable = false)
    private String provider;

    @Column(nullable = false)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "photo_link")
    private String photoLink;

    @Lob
    @Column(name = "access_token", columnDefinition = "TEXT", nullable = false)
    private String accessToken;

    @Lob
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @Column(name = "site_url")
    private String siteUrl;

    @Column(name = "folder_path")
    private String folderPath;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_base_id", insertable = false, updatable = false)
    private KnowledgeBase knowledgeBase;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
