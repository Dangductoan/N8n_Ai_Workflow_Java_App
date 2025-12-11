package ntt.common.api.shared.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ntt.common.api.shared.AbstractBaseModel;


@Getter
@Setter
@AllArgsConstructor
public class KBSpacesDto extends AbstractBaseModel {

    private String companyBucket;
    private String qdrantCollectionName;
    private Integer kbId;
    private String kbName;
    private Integer  projectId;
    private Integer  userId;
}
