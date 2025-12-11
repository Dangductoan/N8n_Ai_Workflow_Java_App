package ntt.common.api.datastore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonStorageResponse {

    private Integer code;
    private String message;
    private Boolean status;

}