package ntt.common.api.shared.tenant.dto;

import lombok.Getter;
import lombok.Setter;
import ntt.common.api.shared.AbstractBaseModel;

@Getter
@Setter
public class OrganizationUnitDto extends AbstractBaseModel {
    private Integer id;

    private String uuid;

    private String parentUuid;

    private String name;

    private String type;

    private Integer nodePosX;

    private Integer nodePosY;
}
