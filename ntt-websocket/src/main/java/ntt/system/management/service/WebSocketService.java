/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-12
 * Description : Create WebSocketService for BE communicate with FE
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.system.management.service;

import ntt.common.api.websocket.WebSocketMessage;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ntt.system.management.domain.StompPrincipal;

import java.util.*;

@Data
@Slf4j
@Service
@Getter
@Setter
public class WebSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String SIMP_SESSION_ID = "simpSessionId";
    private static final String WS_MESSAGE_TRANSFER_DESTINATION = "/topic/notification";
    private HashMap<String, String> mapSessionUser = new HashMap<String, String>();

    /**
     * Handles WebSocket connection events
     */
    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event) {
        String sessionId = getSessionIdFromMessageHeaders(event);
        StompPrincipal user = (StompPrincipal) event.getUser();
        String userId = user != null? user.getId(): null;
        this.mapSessionUser.put(sessionId, userId);
        log.info(String.format("WebSocket connection established for sessionId: %1$s userId: %2$s", sessionId, userId));
    }

    /**
     * Handles WebSocket disconnection events
     */
    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = getSessionIdFromMessageHeaders(event);
        log.info(String.format("WebSocket connection closed for sessionId %s", sessionId));
        //Remove session when disconnection
        this.mapSessionUser.remove(sessionId);
    }

    WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public Boolean sendToUser(String userId, WebSocketMessage message) {
        if(StringUtils.isEmpty(userId) || message == null) return false;
        Boolean status = false;
        message.setUserId(userId);
        for(Map.Entry<String, String> entry: mapSessionUser.entrySet()){
            String sessionId = entry.getKey();
            String runningUserId = entry.getValue();
            if(StringUtils.isEmpty(runningUserId)) continue;
            if(userId.equalsIgnoreCase(runningUserId)) {
                MessageHeaders headers = this.createHeaders(sessionId);
                simpMessagingTemplate.convertAndSendToUser(sessionId, WS_MESSAGE_TRANSFER_DESTINATION, message, headers);
                status = true;
            }
        }
        return status;
    }

    public void sendToSession(String sessionId, WebSocketMessage message) {
        if(StringUtils.isEmpty(sessionId) || message == null) return;
        MessageHeaders headers = this.createHeaders(sessionId);
        simpMessagingTemplate.convertAndSendToUser(sessionId, WS_MESSAGE_TRANSFER_DESTINATION, message, headers);
    }

    public Boolean broadcast(WebSocketMessage message) {
        if(message == null) return false;
        Boolean status = false;
        for(Map.Entry<String, String> entry: mapSessionUser.entrySet()){
            String sessionId = entry.getKey();
            MessageHeaders headers = this.createHeaders(sessionId);
            simpMessagingTemplate.convertAndSendToUser(sessionId, WS_MESSAGE_TRANSFER_DESTINATION, message, headers);
            status = true;
        }
        return status;
    }

    public void addUser(String sessionId, String userId) {
        if(StringUtils.isEmpty(sessionId) || userId == null) return;
        this.mapSessionUser.put(sessionId, userId);
    }

    public void removeUser(String userId){
        if(StringUtils.isEmpty(userId)) return;
        for(Map.Entry<String, String> entry: mapSessionUser.entrySet()){
            String sessionId = entry.getKey();
            String runningUserId = entry.getValue();
            if(StringUtils.isEmpty(runningUserId)) continue;
            if(userId.equalsIgnoreCase(runningUserId)){
                mapSessionUser.remove(sessionId);
            }
        }
    }


    private MessageHeaders createHeaders(String sessionId) {
        if(StringUtils.isEmpty(sessionId)) return null;
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    private String getSessionIdFromMessageHeaders(SessionDisconnectEvent event) {
        Map<String, Object> headers = event.getMessage().getHeaders();
        return Objects.requireNonNull(headers.get(SIMP_SESSION_ID)).toString();
    }

    private String getSessionIdFromMessageHeaders(SessionConnectEvent event) {
        Map<String, Object> headers = event.getMessage().getHeaders();
        return Objects.requireNonNull(headers.get(SIMP_SESSION_ID)).toString();
    }



}
