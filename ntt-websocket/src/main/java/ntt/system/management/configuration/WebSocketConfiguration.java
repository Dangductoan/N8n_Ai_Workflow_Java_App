package ntt.system.management.configuration;

import ntt.system.management.handler.PrincipalHandshakeHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.addresses:#{'localhost'}}")
    private String brokerRelayHost;

    @Value("${spring.rabbitmq.port:#{61613}}")
    private Integer brokerRelayPort;

    @Value("${spring.rabbitmq.username:#{'guest'}}")
    private String clientLogin;

    @Value("${spring.rabbitmq.password:#{'guest'}}")
    private String clientPasscode;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //-------------------------------------------------------------------------------------------------------------
        // According to the doc of RabbitMQ STOMP, only destination starting with /exchange, /queue, /amq/queue,
        // /topic and temp-queue are allowed.
        //-------------------------------------------------------------------------------------------------------------
        // exchange -- SEND to arbitrary routing keys and SUBSCRIBE to arbitrary binding patterns;
        // queue -- SEND and SUBSCRIBE to queues managed by the STOMP gateway;
        // amq/queue -- SEND and SUBSCRIBE to queues created outside the STOMP gateway;
        // topic -- SEND and SUBSCRIBE to transient and durable topics;
        // temp-queue/ -- create temporary queues (in reply-to headers only).

        registry.enableStompBrokerRelay( "/exchange", "/queue", "/amq/queue", "/topic")
                .setClientLogin(clientLogin)
                .setClientPasscode(clientPasscode)
                .setRelayHost(brokerRelayHost)
                .setRelayPort(brokerRelayPort);

        //registry.enableSimpleBroker("/exchange", "/queue", "/topic");

        // This configuration allows Spring to understand that any message sent to a WebSocket channel name prefixed
        // with /app should be routed to a @MessageMapping in our application.
        // registry.setApplicationDestinationPrefixes("/api");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*
         * This configures a STOMP (Simple Text Oriented Messaging Protocol)
         * endpoint for our websocket to be hosted on
         */
        registry
                .addEndpoint("/websocket")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new PrincipalHandshakeHandler());

        /*
         * This configures an endpoint with a fallback for SockJS in case the
         * client (an old browser) doesn't support WebSockets natively
         */
        registry
                .addEndpoint("/sockjs")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new PrincipalHandshakeHandler())
                .withSockJS();


    }


}
