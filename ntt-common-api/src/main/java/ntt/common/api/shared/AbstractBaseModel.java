package ntt.common.api.shared;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AbstractBaseModel {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}