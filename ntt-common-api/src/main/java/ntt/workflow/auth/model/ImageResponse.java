package ntt.workflow.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageResponse {

    private String id;
    private String name;
    private String type;
    private String category;
    private String baseUrl;
    private String uri;
    private String path;
    private String message;
    private String mediaType;
    private byte[] data;

}