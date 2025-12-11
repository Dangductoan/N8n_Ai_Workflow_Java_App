package ntt.common.jpa.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public class AbstractBaseEntity implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(50)")
    private String id;

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public AbstractBaseEntity() {
    }

    @PrePersist
    public void beforeInsert() {
        if(StringUtils.isEmpty(this.id)) {
            this.id = UUID.randomUUID().toString();
            this.createdAt = new Timestamp(System.currentTimeMillis());
            this.updatedAt = new Timestamp(System.currentTimeMillis());
        }
    }
}
