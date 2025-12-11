package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_configs", indexes = {
        @Index(name = "idx_config_key", columnList = "config_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "config_key", length = 255, unique = true, nullable = false)
    private String configKey;

    @Lob
    @Column(name = "config_value", columnDefinition = "TEXT", nullable = false)
    private String configValue;

    @Builder.Default
    @Column(name = "config_type", length = 50)
    private String configType = "string";

    @Builder.Default
    @Column(length = 100)
    private String category = "general";

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_editable")
    private Boolean isEditable = true;

    @Builder.Default
    @Column(name = "is_tenant_specific")
    private Boolean isTenantSpecific = false;

    @Builder.Default
    @Column(name = "is_sensitive")
    private Boolean isSensitive = false;

    @Builder.Default
    @Column(name = "is_advanced")
    private Boolean isAdvanced = false;

    @Builder.Default
    @Column(name = "requires_restart")
    private Boolean requiresRestart = false;

    @Lob
    @Column(name = "validation_rules", columnDefinition = "TEXT")
    private String validationRules;

    @Lob
    @Column(name = "default_value", columnDefinition = "TEXT")
    private String defaultValue;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 255)
    private String updatedBy;
}
