package ntt.workflow.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageUploadResponse {
    private String id;
    private String name;
    private String type;
}
