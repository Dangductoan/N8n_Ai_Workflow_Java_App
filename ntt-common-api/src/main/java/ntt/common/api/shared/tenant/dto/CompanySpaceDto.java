package ntt.common.api.shared.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ntt.common.api.shared.AbstractBaseModel;

@Getter
@Setter
@AllArgsConstructor
public class CompanySpaceDto extends AbstractBaseModel {

    private String minioBucketName;
    private String qdrantCollectionName;
    private String mysqlDatabaseName;

}
