package com.wxgame.zqdn.utils;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpClientUtils {

	private static final RestTemplate restTemplate;

	static {
		restTemplate = buildRestTemplate();
	}

	private static CloseableHttpClient acceptsUntrustedCertsHttpClient() {

		// finally, build the HttpClient;
		// -- done!
		CloseableHttpClient client = null;
		try {
			HttpClientBuilder b = HttpClientBuilder.create();

			// setup a Trust Strategy that allows all certificates.
			//
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			}).build();
			b.setSSLContext(sslContext);

			// don't check Hostnames, either.
			// -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(),
			// if you don't want to weaken
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

			// here's the special part:
			// -- need to create an SSL Socket Factory, to use our weakened
			// "trust strategy";
			// -- and create a Registry, to register it.
			//
			SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslSocketFactory).build();

			// now, we create connection-manager using our Registry.
			// -- allows multi-threaded use
			PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			connMgr.setMaxTotal(200);
			connMgr.setDefaultMaxPerRoute(100);
			b.setConnectionManager(connMgr);

			client = b.build();
			return client;
		} catch (Exception e) {

			throw new RuntimeException(e.getMessage(), e);
		}

	}

	private static RestTemplate buildRestTemplate() {
		CloseableHttpClient httpClient = HttpClientUtils.acceptsUntrustedCertsHttpClient();
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		return restTemplate;
	}

	public static RestTemplate getHttpsRestTemplate() {

		return restTemplate;
	}

	public static void main(String[] args) throws Exception {

		RestTemplate restTemplate = getHttpsRestTemplate();
		String result = restTemplate.getForObject(
				"https://api.weixin.qq.com/sns/jscode2session?appid=wx38f356b39d453183&secret=d3c91bd2cc83d2269e66b67a9b70203f&js_code=061Ct5uN04rym52vowuN0spNtN0Ct5up&grant_type=authorization_code",
				String.class);
		
		System.out.println(result);

	}

}
