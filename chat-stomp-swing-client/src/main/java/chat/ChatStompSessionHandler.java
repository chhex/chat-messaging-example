package chat;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import chat.model.Shout;

public class ChatStompSessionHandler implements StompSessionHandler {
	
	private static Logger logger = Logger.getLogger(ChatStompSessionHandler.class);
	
	private ChatStompMessageHandler messageHandler;
	
	private String userSessionId;

	public ChatStompSessionHandler() {
		super();
	}
	
	public void setChatMessageHandler(ChatStompMessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	public void setUserSessionId(String userSessionId) {
		this.userSessionId = userSessionId;
	}

	public Type getPayloadType(StompHeaders stompHeaders) {
		return Shout.class;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		logger.info("Received shout " + payload.toString());
		messageHandler.handleMessage(payload);

	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		session.subscribe("/topic/shouts.all", this);
		session.subscribe("/topic/shouts.usersession." + userSessionId, this);
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		exception.printStackTrace();

	}

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		exception.printStackTrace();

	}


}
