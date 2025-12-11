package ntt.workflow.auth.entity;
import ntt.common.jpa.entity.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmsImage extends AbstractBaseEntity {

    private String name;

    private String type;

    private String category;

    @Lob
    @Column(name = "data", length = 1000)
    private byte[] data;

    private String path;

    private String userId;

    private String uri;


}