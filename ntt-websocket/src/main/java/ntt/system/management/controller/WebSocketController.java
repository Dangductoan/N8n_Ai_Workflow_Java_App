package ntt.system.management.controller;

import ntt.common.api.websocket.WebSocketMessage;
import ntt.system.management.service.WebSocketService;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {
	private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
	private final WebSocketService webSocketService;

	@Value("${server.port}")
	private String port;

	@Autowired
	public WebSocketController(WebSocketService webSocketService){
		this.webSocketService = webSocketService;
	}

	@MessageMapping("/api/broadcast") 	//Receiving messages on the /app/incoming topic
	@SendTo("/topic/broadcast") 			//Broadcast  to all users
	public String duplexBroadcast(WebSocketMessage message, @Header("simpSessionId") String sessionId, Principal principal) {
		logger.info("Received message: {} from user: {} with sessionId: {}", message, principal.getName(), sessionId);
		return String.format("Application sessionId %s responded to your message: \"%s\"", sessionId, message.getPayload());
	}

	@MessageMapping("/api/notification")
	@SendToUser("/topic/notification") 			//Send to user
	public String duplexNotification(
			@RequestBody WebSocketMessage message,
			@Header("simpSessionId") String sessionId,
			Principal principal	) {
		logger.info("Received message: {} from user: {} with sessionId: {}", message, principal.getName(), sessionId);
		return String.format("Application sessionId %s responded to your message: \"%s\"", sessionId, message.getPayload());
	}

	@PostMapping("/api/send/notification")
	public ResponseEntity<Boolean> sendNotification(@RequestParam String userId, @RequestBody WebSocketMessage message) {
		Boolean result = this.webSocketService.sendToUser(userId, message);
		String jsonMessage = new Gson().toJson(message);
		logger.info(String.format("Sent notification(%1$s): %2$s", result, jsonMessage));
		return ResponseEntity.ok(result);
	}

	@PostMapping("/api/broadcast/message")
	public ResponseEntity<Boolean> broadcastMessage(@RequestBody WebSocketMessage message) {
		Boolean result = this.webSocketService.broadcast(message);
		String jsonMessage = new Gson().toJson(message);
		logger.info(String.format("Broadcast message(%1$s): %2$s", result, jsonMessage));
		return ResponseEntity.ok(result);
	}


}
