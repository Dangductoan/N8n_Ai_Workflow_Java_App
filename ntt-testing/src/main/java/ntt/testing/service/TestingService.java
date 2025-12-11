//package cms.testing.service;
//
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//import viettelidc.cms.websocket.WebSocketClient;
//import viettelidc.cms.websocket.WebSocketMessage;
//
//@Service
//@AllArgsConstructor
//public class TestingService {
//    WebSocketClient webSocketClient;
//
//    public void sendMessage(){
//        WebSocketMessage message = new WebSocketMessage();
//        message.setPayload("Welcome to this program");
//        message.setStatus("SUCCESS");
//        message.setAction("CREATED");
//        message.setType("Volume");
//
//        var result = webSocketClient.sendNotification("123", message);
//        Boolean status = true;
//    }
//}
