package chat;

import java.util.concurrent.ExecutionException;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.core.JsonProcessingException;

import chat.model.ChatMessage;

/**
 * Created by nick on 30/09/2015.
 */
public class ChatClient {

	private WebSocketStompClient stompClient;

	private ChatSessionHandler chatStompSessionHandler = new ChatSessionHandler();

	private StompHeaders connectHeaders = new StompHeaders();

	private WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();

	private StompSession stompSession;

	private String host;

	public ChatClient() {
		super();
		WebSocketClient client = new StandardWebSocketClient();
		stompClient = new WebSocketStompClient(client);
		MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
		stompClient.setMessageConverter(messageConverter);
		stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
	}

	public void setChatMessageHandler(ChatMessageHandler messageHandler) {
		chatStompSessionHandler.setChatMessageHandler(messageHandler);
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setUserSessionId(String userSessonId) {
		this.chatStompSessionHandler.setUserSessionId(userSessonId);
	}

	public void connect() {
		ListenableFuture<StompSession> f = stompClient.connect("ws://" + host + "/chat/websocket", webSocketHttpHeaders,
				connectHeaders, chatStompSessionHandler);
		try {
			stompSession = f.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendSout(String shout, String to) throws JsonProcessingException {
		ChatMessage msg = ChatMessage.builder().chatText(shout).to(to).build();
		if (to == null || to.isEmpty()) {
			stompSession.send("/app/chat/all", msg);
		} else {
			stompSession.send("/app/chat/" + to, msg);
		}
	}

}
