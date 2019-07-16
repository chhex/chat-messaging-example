package chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@Profile("relay")
@EnableWebSocketMessageBroker
public class WebSocketRelayConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		StompBrokerRelayRegistration stompBrokerRelayConfig = config.enableStompBrokerRelay("/topic");
		stompBrokerRelayConfig.setRelayHost("172.16.92.137");
		stompBrokerRelayConfig.setRelayPort(61613);
		stompBrokerRelayConfig.setClientLogin("admin");
		stompBrokerRelayConfig.setClientPasscode("test");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat").setAllowedOrigins("*").withSockJS();
	}

}