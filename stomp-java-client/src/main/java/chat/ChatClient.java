package chat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import chat.model.ChatMessage;
import chat.model.Shout;

/**
 * Created by nick on 30/09/2015.
 */
public class ChatClient {

	private static Logger logger = Logger.getLogger(ChatClient.class);


	public ListenableFuture<StompSession> connect() {

		WebSocketClient client = new StandardWebSocketClient();
		WebSocketStompClient stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		StompSessionHandler sessionHandler = new MyStompSessionHandler();
		stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
		StompHeaders connectHeaders = new StompHeaders();
		WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
		return stompClient.connect("ws://localhost:8080/chat/websocket", webSocketHttpHeaders, connectHeaders,
				sessionHandler);
	}

	public void sendSout(StompSession stompSession) throws JsonProcessingException {
		ChatMessage msg = ChatMessage.builder().chatText("Shout from Java").to("ALL").build();
		stompSession.send("/app/chat", msg);
	}

	private class MyStompSessionHandler implements StompSessionHandler {

		public Type getPayloadType(StompHeaders stompHeaders) {
			return Shout.class;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			logger.info("Received shout " + payload.toString());

		}

		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			session.subscribe("/topic/shouts", this);
			session.subscribe("/user/topic/shouts", this);
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



	public static void main(String[] args) throws Exception {
		ChatClient chatClient = new ChatClient();
		ListenableFuture<StompSession> f = chatClient.connect();
		StompSession stompSession = f.get();
		logger.info("Sending Shout" + stompSession);
		chatClient.sendSout(stompSession);
		Thread.sleep(60000);
	}

}
