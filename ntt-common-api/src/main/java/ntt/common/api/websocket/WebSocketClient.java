/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-14
 * Description : Create common Websocket Client
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */

package ntt.common.api.websocket;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//For Docker
@FeignClient("websocket-server")

//For K8s
//@FeignClient(
//        name = "websocket-server",
//        url = "${clients.websocket.url}"
//)

public interface WebSocketClient {
    @RequestMapping(value = "/api/send/notification", method = RequestMethod.POST)
    ResponseEntity<String> sendNotification(@RequestParam("userId") String userId, @RequestBody WebSocketMessage message);

    @RequestMapping(value = "/api/broadcast/message", method = RequestMethod.POST)
    ResponseEntity<String> broadcastMessage(@RequestBody WebSocketMessage message);

}
