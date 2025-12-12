package ntt.common.api.shared.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ntt.common.api.shared.AbstractBaseModel;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSpacesDto extends AbstractBaseModel {
    private String companyBucket;

    private List<String> userFolders;

}
