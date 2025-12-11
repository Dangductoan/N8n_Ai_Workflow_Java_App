package ntt.common.api.websocket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WebSocketMessage {
    private String userId;
    private String action;
    private String status;
    private String payload;
    private String type;
    private String tag;

}
