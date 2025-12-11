package ntt.common.api.shared.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ntt.common.api.shared.AbstractBaseModel;

@Setter
@Getter
@AllArgsConstructor
public class DeleteCompanySpaceDto extends AbstractBaseModel {

    private String minioBucketName;
    private String qdrantCollectionName;
    private String mysqlDatabaseName;
    private boolean deletedProjectCollections;
    private UserSpacesInfoDto userSpacesDeleted;

}
