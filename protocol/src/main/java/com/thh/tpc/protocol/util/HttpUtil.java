/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2013-5-20 16:01 创建
 *
 */
package com.thh.tpc.protocol.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * daidai@yiji.com
 */
public class HttpUtil {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public static final int RESOURCE_NOT_FOUND = 404;
	/**
	 * 连接超时时间
	 */
	private static final int CONNECTION_TIMEOUT = 10000;
	/**
	 * 读超时时间
	 */
	private static final int SO_TIMEOUT = 30000;
	private static final String defaultCharset = "utf-8";
	private static PoolingClientConnectionManager monitoredConnManager;
	private final CloseableHttpClient httpclient;
    private static HttpUtil instance = new HttpUtil();

	private HttpUtil() {
		this(200, 50);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				instance.shutdown();
			}
		});
	}
	
	/**
	 * 
	 * @param maxTotal 总共连接数
	 * @param maxPerRoute 每个目标地址最大连接数
	 */
	private HttpUtil(int maxTotal, int maxPerRoute) {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		Scheme httpsScheme = createHttpsScheme();
		schemeRegistry.register(httpsScheme);
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		
		// 重用连接,尽量使用长连接
		monitoredConnManager = new PoolingClientConnectionManager(schemeRegistry);
		// Increase max total connection to 200
		monitoredConnManager.setMaxTotal(maxTotal);
		// Increase default max connection per route to 20
		monitoredConnManager.setDefaultMaxPerRoute(maxPerRoute);
		HttpParams params = new BasicHttpParams();
		params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
		//禁用重定向
		//        params.setParameter(ClientPNames.HANDLE_REDIRECTS,false);
		httpclient = new DefaultHttpClient(monitoredConnManager, params);
	}
	
	private Scheme createHttpsScheme() {
		SSLContext sslContext = null;
		// set up a TrustManager that trusts everything
		try {
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} }, new SecureRandom());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		SSLSocketFactory sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		return new Scheme("https", 443, sf);
	}
	
	private void shutdown() {
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	public static HttpUtil getInstance() {
		return instance;
	}
	
	/**
	 * 设置连接超时时间,单位秒
	 */
	public HttpUtil connectTimeout(int connectTimeout) {
		HttpParams params = this.httpclient.getParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout * 1000);
		return this;
	}
	
	/**
	 * 设置最大连接数
	 */
	public HttpUtil maxTotal(int maxTotal) {
		PoolingClientConnectionManager connectionManager = (PoolingClientConnectionManager) this.httpclient
			.getConnectionManager();
		connectionManager.setMaxTotal(maxTotal);
		return this;
	}
	
	/**
	 * 设置每个目标站点最大连接数
	 */
	public HttpUtil maxPerRoute(int maxPerRoute) {
		PoolingClientConnectionManager connectionManager = (PoolingClientConnectionManager) this.httpclient
			.getConnectionManager();
		connectionManager.setDefaultMaxPerRoute(maxPerRoute);
		return this;
	}
	
	/**
	 * 设置读超时时间,单位秒
	 */
	public HttpUtil readTimeout(int readTimeout) {
		HttpParams params = this.httpclient.getParams();
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, readTimeout * 1000);
		return this;
	}
	
	/**
	 * 
	 * @param url 请求地址
	 * @param dataMap 数据map
	 * @param charset 编码
	 * @return 返回两个值，第一个为状态码，第二个为body数据
	 */
	public HttpResult post(String url, Map<String, ?> dataMap,  String charset) {
		return post(url, dataMap, null, charset);
	}
	
	/**
	 * 
	 * @param url 请求地址
	 * @param dataMap 数据map
	 * @param charset 编码
	 * @return 返回两个值，第一个为状态码，第二个为body数据
	 */
	public HttpResult post(String url,  Map<String, ?> dataMap,  Map<String, ?> headerMap,
							 String charset) {
		return post(url, dataMap, headerMap, false, charset);
	}
	
	/**
	 * 向url提交数据
	 * @param url 请求地址
	 * @param dataMap 数据参数
	 * @param headerMap header参数
	 * @param enableRedirect 是否启用重定向
	 * @param charset 字符编码
	 * @return 返回结果
	 */
	public HttpResult post(String url,  Map<String, ?> dataMap,  Map<String, ?> headerMap,
							boolean enableRedirect,  String charset) {
		url = normalizeUrl(url);
		charset = checkCharset(charset);
		
		HttpPost post = buildPostRequest(url, dataMap, charset);
		
		return execute(url, post, headerMap, enableRedirect, charset);
	}
	
	private HttpPost buildPostRequest(String url, Map<String, ?> dataMap, String charset) {
		HttpPost post = new HttpPost(url);
		
		StringBuilder sb = new StringBuilder();
		sb.append("请求url:").append(url);
		if (dataMap != null) {
			sb.append("\n参数：[");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Map.Entry<String, ?> entry : dataMap.entrySet()) {
				if (entry.getValue() == null) {
					nvps.add(new BasicNameValuePair(entry.getKey(), null));
				} else {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
				}
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
			}
			try {
				post.setEntity(new UrlEncodedFormEntity(nvps, charset));
			} catch (UnsupportedEncodingException e) {
				logger.error("请求{},编码错误", url, e);
			}
			sb.append("]");
		}
		logger.info(sb.toString());
		return post;
	}
	
	private String checkCharset(String charset) {
		if (Strings.isNullOrEmpty(charset)) {
			charset = defaultCharset;
		}
		return charset;
	}
	
	private String normalizeUrl(String url) {
		Preconditions.checkNotNull(url, "url不能为空");
		if (!url.startsWith("http")) {
			url = "http://" + url;
		}
		return url;
	}
	
	/**
	 * 向目标地址发起http get请求 重定向采用httpclient默认实现,参考
	 * {@link org.apache.http.impl.client.DefaultRedirectStrategy}
	 * 
	 * @param url 请求url
	 * @param dataMap 请求参数,会编码到url中
	 * @param headerMap 请求http header
	 * @param charset 字符编码
	 * @return 请求结果
	 */
	public HttpResult get(String url,  Map<String, ?> dataMap,  Map<String, ?> headerMap,
							 String charset) {
		url = normalizeUrl(url);
		charset = checkCharset(charset);
		
		url = buildGetUrl(url, dataMap);
		
		HttpGet get = new HttpGet(url);
		return execute(url, get, headerMap, false, charset);
	}
	
	private String buildGetUrl(String url, Map<String, ?> dataMap) {
		StringBuilder urlSB = new StringBuilder();
		urlSB.append(url);
		if (dataMap != null && !dataMap.isEmpty()) {
			if (url.contains("?")) {
				urlSB.append("&");
			} else {
				urlSB.append("?");
			}
			for (Map.Entry<String, ?> entry : dataMap.entrySet()) {
				if (entry.getValue() == null) {
					urlSB.append(entry.getKey()).append("=");
				} else {
					urlSB.append(entry.getKey()).append("=").append(entry.getValue());
				}
				urlSB.append("&");
			}
			urlSB.deleteCharAt(urlSB.length() - 1);
			url = urlSB.toString();
		}
		logger.info("请求url：{}", url);
		return url;
	}
	
	public HttpResult execute(String url, HttpRequestBase request,  Map<String, ?> headerMap,
								boolean enableRedirect,  String charset) {
		
		HttpResult result = new HttpResult();
		charset = checkCharset(charset);
		try {
			if (headerMap != null && !headerMap.isEmpty()) {
				for (Map.Entry<String, ?> entry : headerMap.entrySet()) {
					if (entry.getValue() == null) {
						request.setHeader(entry.getKey(), "");
					} else {
						request.setHeader(entry.getKey(), entry.getValue().toString());
					}
				}
				logger.info("http header:{}", headerMap);
			}
			HttpResponse response = httpclient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (enableRedirect && isRedirect(statusCode)) {
				Header locationHeader = response.getFirstHeader("Location");
				if (locationHeader != null) {
					String location = locationHeader.getValue();
					if (location != null) {
						logger.info("重定向请求到:{}", location);
						return get(location, null, null, charset);
					}
				}
				
			}
			result.setBody(EntityUtils.toString(response.getEntity(), charset));
			result.setStatusCode(statusCode);
            result.setHeaders(response.getAllHeaders());
			logger.info("http请求{}成功，返回状态码为:{}", url, statusCode);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (RuntimeException e) {
			if (request != null) {
				request.abort();
			}
			throw e;
		} finally {
			if (request != null) {
				request.releaseConnection();
			}
		}
		return result;
	}
	
	public HttpResult get(String url,  Map<String, ?> dataMap,  String charset) {
		return get(url, dataMap, null, charset);
	}
	
	public HttpResult get(String url,  String charset) {
		return get(url, null, charset);
	}
	
	public HttpResult get(String url) {
		return get(url, null);
	}
	
	/**
	 * post数据 编码为utf-8
	 * @param url 请求地址
	 * @param dataMap 数据map
	 * @return 返回两个值，第一个为状态码，第二个为body数据
	 */
	public HttpResult post(String url,  Map<String, ?> dataMap) {
		return post(url, dataMap, defaultCharset);
	}
	
	public HttpResult post(String url) {
		return post(url, null);
	}
	
	/**
	 * 参考: http://en.wikipedia.org/wiki/HTTP_location
	 * @param statusCode http请求响应码
	 * @return 是否为重定向响应
	 */
	private boolean isRedirect(int statusCode) {
		return String.valueOf(statusCode).startsWith("3");
	}
	
	public HttpClient getHttpclient() {
		return httpclient;
	}
	
	public static class HttpResult {
		private int statusCode;
		private String body;

        private Header[] headers;
		
		public HttpResult() {
			super();
		}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof HttpResult)) return false;

            HttpResult result = (HttpResult) o;

            if (statusCode != result.statusCode) return false;
            if (body != null ? !body.equals(result.body) : result.body != null) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(headers, result.headers);

        }

        @Override
        public int hashCode() {
            int result = statusCode;
            result = 31 * result + (body != null ? body.hashCode() : 0);
            result = 31 * result + (headers != null ? Arrays.hashCode(headers) : 0);
            return result;
        }


        public int getStatusCode() {
			return statusCode;
		}
		
		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}
		
		public String getBody() {
			return body;
		}
		
		public void setBody(String body) {
			this.body = body;
		}

        public Header[] getHeaders() {
            return headers;
        }

        public void setHeaders(Header[] headers) {
            this.headers = headers;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("HttpResult{");
            sb.append("body='").append(body).append('\'');
            sb.append(", statusCode=").append(statusCode);
            sb.append(", headers=").append(Arrays.toString(headers));
            sb.append('}');
            return sb.toString();
        }
    }
}
