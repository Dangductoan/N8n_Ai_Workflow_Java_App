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
public class UserSpacesInfoDto extends AbstractBaseModel {

    private String companyBucket;

    private int deletedUserObjectsCount;
}
