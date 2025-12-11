package ntt.user.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class KnowledgeBaseUserLinkId implements Serializable {

    @Column(name = "knowledge_base_id")
    private Integer knowledgeBaseId;

    @Column(name = "user_id")
    private Integer userId;
}