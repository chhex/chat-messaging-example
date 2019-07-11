package chat;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpTests {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		HttpHost targetHost = new HttpHost("localhost", 8080, "http");
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("u21", "u21");
		provider.setCredentials(AuthScope.ANY, credentials);
		AuthCache authCache = new BasicAuthCache();
		authCache.put(targetHost, new BasicScheme());
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(provider);
		context.setAuthCache(authCache);

		BasicCookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		HttpGet securedResource = new HttpGet("http://localhost:8080");
		HttpResponse httpResponse = httpclient.execute(securedResource, context);
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
		HttpResponse csrfResponse = httpclient.execute(csrfResource, context);
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

	}

}
