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
	
	private CloseableHttpClient httpClient;
	
	private BasicCookieStore cookieStore;

	public ListenableFuture<StompSession> connect(CsrfTokenBean csrfToken) {

		WebSocketClient client = new StandardWebSocketClient();
		WebSocketStompClient stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		StompSessionHandler sessionHandler = new MyStompSessionHandler();
		stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
		StompHeaders connectHeaders = new StompHeaders();
		connectHeaders.add(csrfToken.getHeaderName(), csrfToken.getToken());
		WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
		for (Cookie cookie : cookieStore.getCookies()) {
			System.out.println(cookie.getName() + ":" + cookie.getValue());
			webSocketHttpHeaders.add(cookie.getName(), cookie.getValue());
		}
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

	private CsrfTokenBean login(String user) throws IOException, URISyntaxException {
		HttpHost targetHost = new HttpHost("localhost", 8080, "http");
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("u21", "u21");
		provider.setCredentials(AuthScope.ANY, credentials);
		AuthCache authCache = new BasicAuthCache();
		authCache.put(targetHost, new BasicScheme());
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(provider);
		context.setAuthCache(authCache);

		cookieStore = new BasicCookieStore();
		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		HttpGet securedResource = new HttpGet("http://localhost:8080");
		HttpResponse httpResponse = httpClient.execute(securedResource, context);
		HttpEntity responseEntity = httpResponse.getEntity();
		String strResponse = EntityUtils.toString(responseEntity);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		EntityUtils.consume(responseEntity);
		for (Cookie cookie : cookieStore.getCookies()) {
			System.out.println(cookie.getName() + ":" + cookie.getValue());
		}

		System.out.println("Http status code for Request: " + statusCode);// Statue code should be 200
		System.out.println("Response for Request: \n" + strResponse); // Should be login page
		System.out.println("================================================================\n");

		HttpGet csrfResource = new HttpGet("http://localhost:8080/csrf");
		HttpResponse csrfResponse = httpClient.execute(csrfResource, context);
		responseEntity = csrfResponse.getEntity();
		strResponse = EntityUtils.toString(responseEntity);
		statusCode = httpResponse.getStatusLine().getStatusCode();
		EntityUtils.consume(responseEntity);
		System.out.println("Http status code for CSRF Request: " + statusCode);// Status code should be 200
		System.out.println("Response for CSRF Request: \n" + strResponse);// Should be actual page
		System.out.println("================================================================\n");
		ObjectMapper mapper = new ObjectMapper();
		CsrfTokenBean csrfToken = mapper.readValue(strResponse, CsrfTokenBean.class);
		System.out.println("CsrfTokenBean for CSRF Request: " + csrfToken.toString());
		
		return csrfToken;
	}

	public String getCookieValue(CookieStore cookieStore, String cookieName) {
		String value = null;
		for (Cookie cookie : cookieStore.getCookies()) {
			System.out.println(cookie.getName() + ":" + cookie.getValue());
			if (cookie.getName().equals(cookieName)) {
				value = cookie.getValue();
				break;
			}
		}
		return value;
	}

	private void logout() throws IOException {
		httpClient.close();
	}

	public static void main(String[] args) throws Exception {
		ChatClient chatClient = new ChatClient();
		CsrfTokenBean csrfToken = chatClient.login("u21");
		ListenableFuture<StompSession> f = chatClient.connect(csrfToken);
		StompSession stompSession = f.get();

		logger.info("Sending Shout" + stompSession);
		chatClient.sendSout(stompSession);
		Thread.sleep(60000);
		chatClient.logout();
	}

}
