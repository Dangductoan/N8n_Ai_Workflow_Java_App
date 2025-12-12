package ntt.common.api.shared.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ntt.common.api.shared.AbstractBaseModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSpacesDto extends AbstractBaseModel {

    private String companyBucket;

    private String qdrantCollectionName;

    private String projectUUID;

    private Integer projectId;

}
