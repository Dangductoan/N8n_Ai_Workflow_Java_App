/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-12
 * Description : In WebSocket communication, the handshake is a special HTTP
 * request/response exchange that upgrades a standard HTTP connection to a
 * persistent WebSocket connection.
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.system.management.handler;

import ntt.system.management.domain.StompPrincipal;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * Set anonymous user (Principal) in WebSocket messages by using UUID
 * This is necessary to avoid broadcasting messages but sending them to specific user sessions
 */
public class PrincipalHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // generate user name by UUID
        String query = request.getURI().getQuery();
        String userId = UUID.randomUUID().toString();
        String userName = UUID.randomUUID().toString();
        if(!StringUtils.isEmpty(query)) {
            String[] pair = query.split("=");
            if(pair.length == 2 && "userid".equalsIgnoreCase(pair[0])){
                userId = pair[1];
            }
        }
        return new StompPrincipal(userId, userName);
    }


}