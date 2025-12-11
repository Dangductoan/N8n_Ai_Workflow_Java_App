package ntt.common.api.shared.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ntt.common.api.shared.AbstractBaseModel;


@Getter
@Setter
@AllArgsConstructor
public class ProjectSpacesRequestDto extends AbstractBaseModel {

    private String companyUUID;
    private String projectUUID;
    private String projectName;
    private Integer projectId;
    private String companyName;
}
