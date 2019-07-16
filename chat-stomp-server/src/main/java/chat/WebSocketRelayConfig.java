package chat;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${rabbit.host}")
	String relayHost;

	@Value("${rabbit.port}")
	Integer relayPort;

	@Value("${rabbit.user}")
	String relayHostUser;

	@Value("${rabbit.pw}")
	String relayHostPw;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		StompBrokerRelayRegistration stompBrokerRelayConfig = config.enableStompBrokerRelay("/topic");
		stompBrokerRelayConfig.setRelayHost(relayHost);
		stompBrokerRelayConfig.setRelayPort(relayPort);
		stompBrokerRelayConfig.setClientLogin(relayHostUser);
		stompBrokerRelayConfig.setClientPasscode(relayHostPw);
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat").setAllowedOrigins("*").withSockJS();
	}

}