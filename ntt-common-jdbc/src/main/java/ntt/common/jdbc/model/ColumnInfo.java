package ntt.common.jdbc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnInfo {
    public String columnName;
    public String dataType;
    public String maxLength;
    public Boolean isNullable;

}
