package chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class SocketSecurityConfig 
  extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
          .simpDestMatchers("/chat/**").authenticated()
          .simpDestMatchers("/app/chat/**").authenticated()
          .simpSubscribeDestMatchers("/user/topic/**").authenticated()
          .simpSubscribeDestMatchers("/topic/**").authenticated()
          .anyMessage().authenticated();
    }   
  
}