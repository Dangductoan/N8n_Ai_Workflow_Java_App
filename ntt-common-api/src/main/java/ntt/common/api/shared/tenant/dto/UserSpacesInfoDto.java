package ntt.common.api.shared.tenant.dto;

import lombok.Getter;
import lombok.Setter;
import ntt.common.api.shared.AbstractBaseModel;

@Getter
@Setter
public class UserSpacesInfoDto extends AbstractBaseModel {

    private String companyBucket;

    private int deletedUserObjectsCount;
}
