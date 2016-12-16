package com.cic.datacrawl.crawler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.cic.datacrawl.core.StatusCode;

public class HttpCrawler extends WebCrawler {
	private static final Logger LOG = Logger.getLogger(HttpCrawler.class);

	private HttpClient httpClient;
	private int connTimeOut = 5000;
	private int socketTimeOut = 5000;
	/**
     * useCookieStore = 1 使用CookieStore的方式储存Cookie
     * <Br>
     * useCookieStore = 0 不使用CookieStore的方式储存Cookie
     */
	private int useCookieStore = 0;
	
	
	public int getUseCookieStore() {
        return useCookieStore;
    }

    public void setUseCookieStore(int useCookieStore) {
        if(useCookieStore == 1 && cookieStore == null){
            cookieStore = new BasicCookieStore();
        }
        this.useCookieStore = useCookieStore;
    }

    public int getConnTimeOut() {
		return connTimeOut;
	}

	public void setConnTimeOut(int connTimeOut) {
		this.connTimeOut = connTimeOut;
	}

	public int getSocketTimeOut() {
		return socketTimeOut;
	}

	public void setSocketTimeOut(int socketTimeOut) {
		this.socketTimeOut = socketTimeOut;
	}
	
	private String cookie = "";

    public void init() {
		httpClient = new HttpClient();
		userAgent = UserAgentUtil.getUserAgent();
		//userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4";
		//设置连接超时和socket超时
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connTimeOut);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(socketTimeOut);
	}
	
	/*
	 * 返回Cookie，保存登录返回的页面，如果是html要转换为XML，供后续判断登录是否成功使用
	 * 只处理http返回的错误，html2xml转换的错误忽略（出错，则xml=html）
	 */
	public String login(String loginUrl, String rUrl, Map<String, String> postData) {
		if(loginUrl == null || loginUrl.length() == 0 || postData == null || postData.isEmpty()) {
			this.errorCode = StatusCode.STATUS_HTTP_NONE_POST_PARAMETER;
			return null;
		}
		
		//String  cookie = loginToWeb(loginUrl, rUrl, postData);;
		//String  cookie = null;
		cookie = "";
		if(useCookieStore == 1){
		    cookie = loginToWebByCookieStore(loginUrl, rUrl, postData);
		}else{
		    cookie = loginToWeb(loginUrl, rUrl, postData);
		}
		
		int retryTimes = 0;
		while(errorCode == StatusCode.STATUS_HTTP_IO_EXCEPTION && retryTimes < 2) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			++retryTimes;
			//cookie = loginToWeb(loginUrl, rUrl, postData);
			if(useCookieStore == 1){
	            cookie = loginToWebByCookieStore(loginUrl, rUrl, postData);
	        }else{
	            cookie = loginToWeb(loginUrl, rUrl, postData);
	        }
		}
		if(this.errorCode != StatusCode.STATUS_SUCC) {
			return null;
		}
				
		parsePageToDocument();
		if(this.errorCode != StatusCode.STATUS_SUCC) {
			return cookie;
		}
		
		convertPageToXml();
		
		//LOG.error(this.xmlContent);
		
		processInvalidCharacter();
		if(webCharset == null && (invalidTagReplaceList != null || invalidAttrList != null)) {
			processXmlInvalidTag();
		}
		
		return cookie;
	}
	
	public void downloadPage(String baseUrl, String rUrl, String cookie, Map<String,String> postData) {
	    if(useCookieStore == 0)
	    {
	        downPageByPost(baseUrl, rUrl, cookie, postData);
	    }else{
	        downloadPageByPostAndCookieStore(baseUrl, rUrl, cookie, postData);
	    }
	    
		int retryTimes = 0;
		while(errorCode == StatusCode.STATUS_HTTP_IO_EXCEPTION && retryTimes < 2) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			++retryTimes;
			//downPageByPost(baseUrl, rUrl, cookie, postData);
			if(useCookieStore == 0)
	        {
	            downPageByPost(baseUrl, rUrl, cookie, postData);
	        }else{
	            downloadPageByPostAndCookieStore(baseUrl, rUrl, cookie, postData);
	        }
		}
		
		if(this.errorCode != StatusCode.STATUS_SUCC) {
			LOG.error("Download page by post failed: " + this.errorCode);
			return;
		}

		if(webCharset != null && (invalidTagReplaceList != null || invalidAttrList != null)) {
			processHtmlInvalidTag();
		}
		
		//解析html为Document
		parsePageToDocument();
		if(this.errorCode != StatusCode.STATUS_SUCC) {
			LOG.error("Parse page to document faled: " + this.errorCode);
			return;
		}
		
		//将Document转换为XML字符串
		convertPageToXml();
		
		processInvalidCharacter();
		
		if(webCharset == null && (invalidTagReplaceList != null || invalidAttrList != null)) {
			processXmlInvalidTag();
		}
	}
	
	public void downloadPage(String baseUrl, String rUrl, String cookie) {
		//从网络下载网页html源码
	    if(useCookieStore == 1)
		{
	        downloadPageByGetAndCookieStore(baseUrl, rUrl, cookie);
		}else{
		    downPageByGet(baseUrl, rUrl, cookie);
		}
		//如果是IO异常，则重试两次
		int retryTimes = 0;
		while(errorCode == StatusCode.STATUS_HTTP_IO_EXCEPTION && retryTimes < 2) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			++retryTimes;
			//downPageByGet(baseUrl, rUrl, cookie);
			if(useCookieStore == 1)
	        {
	            downloadPageByGetAndCookieStore(baseUrl, rUrl, cookie);
	        }else{
	            downPageByGet(baseUrl, rUrl, cookie);
	        }
		}
		
		if(this.errorCode != StatusCode.STATUS_SUCC) {
			LOG.error("Download page by get failed: " + errorCode);
			return;
		}
		
		if(webCharset != null && (invalidTagReplaceList != null || invalidAttrList != null)) {
			processHtmlInvalidTag();
		}
		
		//解析html为Document
		parsePageToDocument();
		if(this.errorCode != StatusCode.STATUS_SUCC) {
			LOG.error("parse page to document failed: " + errorCode);
			return;
		}
		
		//将Document转换为XML字符串
		convertPageToXml();
		
		processInvalidCharacter();
		
		if(webCharset == null && (invalidTagReplaceList != null || invalidAttrList != null)) {
			processXmlInvalidTag();
		}
	}
	
	
	public String loginToWeb(String loginUrl, String rUrl, Map<String,String> postData) {
		this.html = null;
		this.xmlContent = null;
		
		PostMethod method = new PostMethod(loginUrl);
		if(rUrl != null && rUrl.trim().length() > 0) {
			method.setRequestHeader("Referer", rUrl);
		}
	
		LOG.info(loginUrl);
		LOG.info(rUrl);
	
		if(postData != null && !postData.isEmpty()) {
			NameValuePair[] datas = new NameValuePair[postData.size()];
			int index = 0;
			Set<String> keySet = postData.keySet();
			Iterator<String> iter = keySet.iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				datas[index] = new NameValuePair();
				datas[index].setName(key);
				datas[index].setValue(postData.get(key));
				++index;
				
				LOG.info(key+":"+postData.get(key));
			}
			
			method.setRequestBody(datas);
		}else{
			LOG.error("postData is empty");
		}
		
		LOG.info("conntimeout: " + connTimeOut);
		LOG.info("so_timeout:" + socketTimeOut);
		
		method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		method.getParams().setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, charset);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET , charset);
		method.getParams().setParameter(HttpMethodParams.HTTP_URI_CHARSET, charset);
		method.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connTimeOut);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, socketTimeOut);
		method.getParams().setParameter(HttpMethodParams.USER_AGENT, userAgent);
		if(useGzip == 1)
		{
		    method.setRequestHeader("Accept-Encoding", "gzip");
		}
		method.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		//注意：POST方法禁止重定向
		method.setFollowRedirects(false);

		String cookies = null;
		try {
			int result = httpClient.executeMethod(method); 
			
			this.setHttpStatus(result);
			if (200 == result) {
				Header contentEncodingHeader = method.getResponseHeader("Content-Encoding");
				if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
					InputStream inStream = method.getResponseBodyAsStream();
					GZIPInputStream gzipStream = new GZIPInputStream(inStream);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int bufSize = 1024;
					byte [] htmlBuf = new byte[bufSize];
					int len = 0;
					while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
						baos.write(htmlBuf, 0, len);
					}
					
					this.html = baos.toByteArray();
					this.xmlContent = baos.toString(charset);
					
					//LOG.info("xmlContent:" + this.xmlContent);
					
					gzipStream.close();
					baos.close();
				} else {
					this.html = method.getResponseBody();
					this.xmlContent = method.getResponseBodyAsString();
					//LOG.info("xmlContent:" + this.xmlContent);
				}
				
				//提取 cookies
				Header[] cookieList = method.getResponseHeaders("Set-Cookie");
				if(cookieList != null && cookieList.length > 0)
				{
					for(Header cookie : cookieList) {
	            		cookies += cookie.getName() + "="+cookie.getValue() + ";";
	            	}
					LOG.info("cookies:" + cookies);
				} else {
					Cookie[] cookiesArray = httpClient.getState().getCookies();
		            if(cookiesArray != null && cookiesArray.length > 0) {
						for(Cookie c:cookiesArray){
			                cookies += c.toString()+";";
			            }
		            }
				}
				
				if(cookies == null || cookies.isEmpty()) {
					this.errorCode = StatusCode.STATUS_HTTP_PARSE_COOKIE_FAILED;
					this.errorMessage = "Get login cookie failed";
				} else {
					this.errorCode = StatusCode.STATUS_SUCC;
				}
				
				//cookie = cookies;
				
				//提取webCharset
				if(webCharset == null) {
					boolean flag = false;
					Header charsetTypeHeader = method.getResponseHeader("Content-Type");
					if(charsetTypeHeader != null) {
						String contentType = charsetTypeHeader.getValue().toLowerCase();
						if(contentType.indexOf("charset=") > -1) {
							Pattern pattern = Pattern.compile("charset=([^ ]+)");
							Matcher matcher = pattern.matcher(contentType);
							if(matcher.find()) {
								webCharset = matcher.group(1);
								flag = true;
							}
						}
					}
					
					if(!flag) {
						String htmlWeb = new String(html).toLowerCase();
						Pattern pattern = Pattern.compile("<meta [^>]*content=\"text/html; charset=([^\"]+)\"");
						Matcher matcher = pattern.matcher(htmlWeb);
						if(matcher.find()) {
							webCharset = matcher.group(1);
						} else {
							pattern = Pattern.compile("<meta[^>]*charset=[\"]?([^>]+)[\"]?");
							matcher = pattern.matcher(htmlWeb);
							if(matcher.find()) {
								webCharset = matcher.group(1);
							}
						}
					}
					
					if(webCharset != null) {
						webCharset = webCharset.replaceAll("/", "").replaceAll("\"", "");
						if(!webCharset.equals("utf-8") && !webCharset.equals("utf8")) {
							String tmp = new String(html, webCharset);
							html = tmp.getBytes(charset);
						}
					}
				}
			} else {
				LOG.error(" HttpClient Result Status: " + result + " for url: " + loginUrl);
				ungzipByPostMethod(method);
				if(result < 200) {
					this.errorCode = StatusCode.STATUS_HTTP_INNER_SAVE;
				} else if(result > 200 && result < 300) {
					this.errorCode = StatusCode.STATUS_HTTP_RES_BROKEN;
				} else if(result < 400) {
					this.errorCode = StatusCode.STATUS_HTTP_REQ_REDIRECT;
//如果登陆时有跳转，说明登陆失败
//					String location = method.getResponseHeader("Location").getValue();
//					if(location.indexOf("http://")==-1 && location.indexOf("https://")==-1){
//						String host = method.getRequestHeader("Host").getValue();
//						location = URLUtil.mergeURL(host, location);
//						
//						Header charsetTypeHeader = method.getResponseHeader("Content-Type");
//						if(charsetTypeHeader != null) {
//							String contentType = charsetTypeHeader.getValue().toLowerCase();
//							if(contentType.indexOf("charset=") > -1) {
//								Pattern pattern = Pattern.compile("charset=([^ ]+)");
//								Matcher matcher = pattern.matcher(contentType);
//								if(matcher.find()) {
//									webCharset = matcher.group(1);
//								}
//							}
//						}
//					}
//					this.errorMessage = location;
//					LOG.error("location: " + location);
//					downloadPage(location, rUrl, null);
				} else if(result < 500) {
					this.errorCode = StatusCode.STATUS_HTTP_REQ_FORBIDDEN;
				} else if(result < 600) {
					this.errorCode = StatusCode.STATUS_HTTP_ROUTE_ERR;
				} else {
					this.errorCode = StatusCode.STATUS_HTTP_UNDEFIND_ERR;
				}
				this.errorMessage = "Login by httpClient, response status: " + result;
			}
		} catch (IOException e) {
			this.errorCode = StatusCode.STATUS_HTTP_IO_EXCEPTION;
			this.errorMessage = "IO Exception for login by httpClient";
			LOG.error("http Exception: " + loginUrl);
			LOG.error(e.getMessage());
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		
		return cookies;
	}
	
	private void downPageByPost(String baseUrl, String rUrl, String cookie, Map<String, String> postData) {
		this.html = null;
		this.xmlContent = null;
		PostMethod method = new PostMethod(baseUrl);
		if(rUrl != null && rUrl.trim().length() > 0) {
			method.setRequestHeader("Referer", rUrl);
		}
		
		if(cookie != null && cookie.trim().length() > 0) {
			method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
			method.setRequestHeader("Cookie", cookie);
		}
		
		if(postData != null && !postData.isEmpty()) {
			NameValuePair[] datas = new NameValuePair[postData.size()];
			int index = 0;
			Set<String> keySet = postData.keySet();
			Iterator<String> iter = keySet.iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				datas[index] = new NameValuePair();
				datas[index].setName(key);
				datas[index].setValue(postData.get(key));
				++index;
			}
			
			method.setRequestBody(datas);
		}
		
		method.getParams().setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, charset);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET , charset);
		method.getParams().setParameter(HttpMethodParams.HTTP_URI_CHARSET, charset);
		method.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connTimeOut);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, socketTimeOut);
		method.getParams().setParameter(HttpMethodParams.USER_AGENT, userAgent);
		if(useGzip == 1)
		{
		    method.setRequestHeader("Accept-Encoding", "gzip");
		}
		method.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		//注意：POST方法禁止重定向
		method.setFollowRedirects(false);
		try {
			int result = httpClient.executeMethod(method); 
			
			getCookieByPostMethod(method);
			
			this.setHttpStatus(result);
			if (200 == result) {
				Header contentEncodingHeader = method.getResponseHeader("Content-Encoding");
				if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
					InputStream inStream = method.getResponseBodyAsStream();
					GZIPInputStream gzipStream = new GZIPInputStream(inStream);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int bufSize = 1024;
					byte [] htmlBuf = new byte[bufSize];
					int len = 0;
					while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
						baos.write(htmlBuf, 0, len);
					}
					
					this.html = baos.toByteArray();
					this.xmlContent = baos.toString(charset);
					gzipStream.close();
					baos.close();
				} else {
					this.html = method.getResponseBody();
					this.xmlContent = method.getResponseBodyAsString();
				}
				
				if(webCharset == null) {
					boolean flag = false;
					Header charsetTypeHeader = method.getResponseHeader("Content-Type");
					if(charsetTypeHeader != null) {
						String contentType = charsetTypeHeader.getValue().toLowerCase();
						if(contentType.indexOf("charset=") > -1) {
							Pattern pattern = Pattern.compile("charset=([^ ]+)");
							Matcher matcher = pattern.matcher(contentType);
							if(matcher.find()) {
								webCharset = matcher.group(1);
								flag = true;
							}
						}
					}
					
					if(!flag) {
						String htmlWeb = new String(html).toLowerCase();
						Pattern pattern = Pattern.compile("<meta [^>]*content=\"text/html; charset=([^\"]+)\"");
						Matcher matcher = pattern.matcher(htmlWeb);
						if(matcher.find()) {
							webCharset = matcher.group(1);
						} else {
							pattern = Pattern.compile("<meta[^>]*charset=[\"]?([^>]+)[\"]?");
							matcher = pattern.matcher(htmlWeb);
							if(matcher.find()) {
								webCharset = matcher.group(1);
							}
						}
					}
					
					if(webCharset != null) {
						webCharset = webCharset.replaceAll("/", "").replaceAll("\"", "");
						if(!webCharset.equals("utf-8") && !webCharset.equals("utf8")) {
							String tmp = new String(html, webCharset);
							html = tmp.getBytes(charset);
						}
					}
				}
				
				this.errorCode = StatusCode.STATUS_SUCC;
			} else {
				LOG.error(" HttpClient Result Status: " + result + " for url: " + baseUrl);
				ungzipByPostMethod(method);
				if(result < 200) {
					this.errorCode = StatusCode.STATUS_HTTP_INNER_SAVE;
				} else if(result > 200 && result < 300) {
					this.errorCode = StatusCode.STATUS_HTTP_RES_BROKEN;
				} else if(result < 400) {
					this.errorCode = StatusCode.STATUS_HTTP_REQ_REDIRECT;
//					String location = method.getResponseHeader("Location").getValue();
//					if(location.indexOf("http://")==-1 && location.indexOf("https://")==-1){
//						String host = method.getRequestHeader("Host").getValue();
//						location = URLUtil.mergeURL(host, location);
//						
//						Header charsetTypeHeader = method.getResponseHeader("Content-Type");
//						if(charsetTypeHeader != null) {
//							String contentType = charsetTypeHeader.getValue().toLowerCase();
//							if(contentType.indexOf("charset=") > -1) {
//								Pattern pattern = Pattern.compile("charset=([^ ]+)");
//								Matcher matcher = pattern.matcher(contentType);
//								if(matcher.find()) {
//									webCharset = matcher.group(1);
//								}
//							}
//						}
//					}
//					this.errorMessage = location;
//					LOG.error("location: " + location);
//					downloadPage(location, rUrl, cookie);
				} else if(result < 500) {
					this.errorCode = StatusCode.STATUS_HTTP_REQ_FORBIDDEN;
				} else if(result < 600) {
					this.errorCode = StatusCode.STATUS_HTTP_ROUTE_ERR;
				} else {
					this.errorCode = StatusCode.STATUS_HTTP_UNDEFIND_ERR;
				}
//				this.errorMessage = "Download page by httpClient with POST method, response status: " + result;
			}

		} catch (IOException e) {
			this.errorCode = StatusCode.STATUS_HTTP_IO_EXCEPTION;
			this.errorMessage = "IO Exception for download page by httpClient with POST method";
			LOG.error("http Exception: " + baseUrl);
			LOG.error(e.getMessage());
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}
	
	private void downPageByGet(String baseUrl, String rUrl, String cookie) {
		this.html = null;
		this.xmlContent = null;
		
		LOG.debug(baseUrl);
		
		GetMethod method = new GetMethod(baseUrl);
		if(rUrl != null && rUrl.trim().length() > 0) {
			method.setRequestHeader("Referer", rUrl);
		}
		if(cookie != null && cookie.trim().length() > 0) {
			method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
			method.setRequestHeader("Cookie", cookie);
		}
		method.getParams().setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, charset);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET , charset);
		method.getParams().setParameter(HttpMethodParams.HTTP_URI_CHARSET, charset);
		method.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connTimeOut);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, socketTimeOut);
		method.getParams().setParameter(HttpMethodParams.USER_AGENT, userAgent);
		if(useGzip == 1)
		{
		    method.setRequestHeader("Accept-Encoding", "gzip");
		}
		method.setFollowRedirects(true);
		method.getParams().setParameter("http.protocol.allow-circular-redirects", true);
		method.getParams().setParameter("http.protocol.max-redirects", 10);
		try {
			int result = httpClient.executeMethod(method); 
			
			getCookieByGetMethod(method);
			
			this.setHttpStatus(result);
			if (200 == result) {
				Header contentEncodingHeader = method.getResponseHeader("Content-Encoding");
				if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
					InputStream inStream = method.getResponseBodyAsStream();
					GZIPInputStream gzipStream = new GZIPInputStream(inStream);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int bufSize = 1024;
					byte [] htmlBuf = new byte[bufSize];
					int len = 0;
					while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
						baos.write(htmlBuf, 0, len);
					}
					
					this.html = baos.toByteArray();
					this.xmlContent = baos.toString(charset);
					gzipStream.close();
					baos.close();
				} else {
					this.html = method.getResponseBody();
					this.xmlContent = method.getResponseBodyAsString();
				}
				
				if(webCharset == null) {
					boolean flag = false;
					Header charsetTypeHeader = method.getResponseHeader("Content-Type");
					if(charsetTypeHeader != null) {
						String contentType = charsetTypeHeader.getValue().toLowerCase();
						if(contentType.indexOf("charset=") > -1) {
							Pattern pattern = Pattern.compile("charset=([^ ]+)");
							Matcher matcher = pattern.matcher(contentType);
							if(matcher.find()) {
								webCharset = matcher.group(1);
								flag = true;
							}
						}
					}
					
					if(!flag) {
						String htmlWeb = new String(html).toLowerCase();
						Pattern pattern = Pattern.compile("<meta [^>]*content=\"text/html; charset=([^\"]+)\"");
						Matcher matcher = pattern.matcher(htmlWeb);
						if(matcher.find()) {
							webCharset = matcher.group(1);
						} else {
							pattern = Pattern.compile("<meta[^>]*charset=[\"]?([^>]+)[\"]?");
							matcher = pattern.matcher(htmlWeb);
							if(matcher.find()) {
								webCharset = matcher.group(1);
							}
						}
					}
					
					if(webCharset != null) {
						webCharset = webCharset.replaceAll("/", "").replaceAll("\"", "");
						LOG.info("webCharset:" + webCharset);
						if(!webCharset.equals("utf-8") && !webCharset.equals("utf8")) {
							String tmp = new String(html, webCharset);
							html = tmp.getBytes(charset);
						}
					}
				}
				
				errorCode = StatusCode.STATUS_SUCC;
			} else {
				LOG.error(" HttpClient Result Status: " + result + " for url: " + baseUrl);
				ungzipByGetMethod(method);
				if(result < 200) {
					errorCode = StatusCode.STATUS_HTTP_INNER_SAVE;
				} else if(result > 200 && result < 300) {
					errorCode = StatusCode.STATUS_HTTP_RES_BROKEN;
				} else if(result < 400) {
					errorCode = StatusCode.STATUS_HTTP_REQ_REDIRECT;
				} else if(result < 500) {
					errorCode = StatusCode.STATUS_HTTP_REQ_FORBIDDEN;
				} else if(result < 600) {
					errorCode = StatusCode.STATUS_HTTP_ROUTE_ERR;
				} else {
					errorCode = StatusCode.STATUS_HTTP_UNDEFIND_ERR;
				}
				errorMessage = "Download page by httpClient with GET method, response status: " + result;
			}

		} catch (IOException e) {
			errorCode = StatusCode.STATUS_HTTP_IO_EXCEPTION;
			errorMessage = "IO Exception for download page by httpClient with GET method; " + e.getMessage();
			LOG.error("http Exception: " + baseUrl);
			LOG.error(e.getMessage());
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}
	
	/**
	 * 解压页面数据
	 * @param method
	 * @throws Exception
	 */
	private void ungzipByGetMethod(GetMethod method){
		try{
			Header contentEncodingHeader = method.getResponseHeader("Content-Encoding");
			if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
				InputStream inStream = method.getResponseBodyAsStream();
				GZIPInputStream gzipStream = new GZIPInputStream(inStream);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int bufSize = 1024;
				byte [] htmlBuf = new byte[bufSize];
				int len = 0;
				while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
					baos.write(htmlBuf, 0, len);
				}
				
				this.html = baos.toByteArray();
				this.xmlContent = baos.toString(charset);
				gzipStream.close();
				baos.close();
			}else{
				this.html = method.getResponseBody();
				this.xmlContent = method.getResponseBodyAsString();
			}
		}catch(Exception e){
			
		}
	}
	
	/**
	 * 解压页面数据
	 * @param method
	 * @throws Exception
	 */
	private void ungzipByPostMethod(PostMethod method){
		try{
			Header contentEncodingHeader = method.getResponseHeader("Content-Encoding");
			if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
				InputStream inStream = method.getResponseBodyAsStream();
				GZIPInputStream gzipStream = new GZIPInputStream(inStream);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int bufSize = 1024;
				byte [] htmlBuf = new byte[bufSize];
				int len = 0;
				while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
					baos.write(htmlBuf, 0, len);
				}
				
				this.html = baos.toByteArray();
				this.xmlContent = baos.toString(charset);
				gzipStream.close();
				baos.close();
			} else {
				this.html = method.getResponseBody();
				this.xmlContent = method.getResponseBodyAsString();
			}
		}catch(Exception e){
			
		}
	}
	
	
	/**
	 * 保存cookie的神器
	 */
	private CookieStore cookieStore;

    /**
     * 
     * <一句话功能简述>
     * 利用CookieStore进行登录操作
     * <功能详细描述>
     * @author   charles.chen
     * @param loginUrl
     * @param rUrl
     * @param postData
     * @return
     * @see [类、类#方法、类#成员]
     */
    public String loginToWebByCookieStore(String loginUrl, String rUrl, Map<String,String> postData) {
        this.html = null;
        this.xmlContent = null;
        
        org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();//(new ThreadSafeClientConnManager());
        
        
        HttpResponse response = null;
        HttpPost httpPost = new HttpPost(loginUrl);
        
        if(rUrl != null && rUrl.trim().length() > 0) {
            httpPost.setHeader("Referer", rUrl);
        }
    
        LOG.info(loginUrl);
        LOG.info(rUrl);
        
        LOG.info("conntimeout: " + connTimeOut);
        LOG.info("so_timeout:" + socketTimeOut);
        
        HttpParams params = new BasicHttpParams(); 
        params.setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, charset);
        params.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET , charset);
        params.setParameter(HttpMethodParams.HTTP_URI_CHARSET, charset);
        params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connTimeOut);
        params.setParameter(HttpMethodParams.SO_TIMEOUT, socketTimeOut);
        params.setParameter(HttpMethodParams.USER_AGENT, userAgent);
        
        if(useGzip == 1)
        {
            httpPost.setHeader("Accept-Encoding", "gzip");
        }
        
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        httpPost.setParams(params);
        
        HttpEntity postBodyEnt = null;
        try {
            List<org.apache.http.NameValuePair> list = new ArrayList<org.apache.http.NameValuePair>();
            if(postData != null && !postData.isEmpty()) {
                Set<String> keySet = postData.keySet();
                Iterator<String> iter = keySet.iterator();
                while(iter.hasNext()) {
                    String key = iter.next();
                    org.apache.http.NameValuePair nameValuePair = new BasicNameValuePair(key, postData.get(key));
                    list.add(nameValuePair);
                    LOG.info(key+":"+postData.get(key));
                }
            }else{
                LOG.warn("postData is empty");
            }
            postBodyEnt = new UrlEncodedFormEntity(list);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        
        /*if(postBodyEnt == null){
            LOG.error("postBodyEnt is null");
            return null;
        }*/
        
        
        httpPost.setEntity(postBodyEnt);
        
        HttpUriRequest request = httpPost;
        
        
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        try {
            response = httpClient.execute(request, localContext);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
            LOG.error(e1);
        } catch (IOException e1) {
            e1.printStackTrace();
            LOG.error(e1);
        }
        
        
        if(response == null){
            LOG.error("response is null");
            return null;
        }
        
        List<org.apache.http.cookie.Cookie> cookieList = cookieStore.getCookies();
        String cookieStr = "";
        for(org.apache.http.cookie.Cookie cookie : cookieList){
            cookieStr += (cookie.getName() + "=" + cookie.getValue()) + ";";
        }
        LOG.info("cookieStr:" + cookieStr);
        
        String cookies = cookieStr;
        try {
            /*baiduLoginService.setPostData(postData);
            HttpResponse response = baiduLoginService.login(loginUrl,rUrl);
            if(response == null){
                return null;
            }
            
            cookies = baiduLoginService.getCookie();*/
            
            int result = response.getStatusLine().getStatusCode(); 
            HttpEntity entity = response.getEntity();
            this.setHttpStatus(result);
            if (200 == result) {
                org.apache.http.Header contentEncodingHeader = response.getLastHeader("Content-Encoding");//.getResponseHeader("Content-Encoding");
                if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
                    InputStream inStream = entity.getContent();//.getResponseBodyAsStream();
                    GZIPInputStream gzipStream = new GZIPInputStream(inStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufSize = 1024;
                    byte [] htmlBuf = new byte[bufSize];
                    int len = 0;
                    while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
                        baos.write(htmlBuf, 0, len);
                    }
                    
                    this.html = baos.toByteArray();
                    this.xmlContent = baos.toString(charset);
                    
                    gzipStream.close();
                    baos.close();
                } else {
                    
                    this.xmlContent = EntityUtils.toString(entity);
                    this.html = xmlContent.getBytes();
                }
                
                
                if(cookies == null || cookies.isEmpty()) {
                    this.errorCode = StatusCode.STATUS_HTTP_PARSE_COOKIE_FAILED;
                    this.errorMessage = "Get login cookie failed";
                } else {
                    this.errorCode = StatusCode.STATUS_SUCC;
                }
                
                //提取webCharset
                if(webCharset == null) {
                    boolean flag = false;
                    org.apache.http.Header charsetTypeHeader = response.getLastHeader("Content-Type");
                    if(charsetTypeHeader != null) {
                        String contentType = charsetTypeHeader.getValue().toLowerCase();
                        if(contentType.indexOf("charset=") > -1) {
                            Pattern pattern = Pattern.compile("charset=([^ ]+)");
                            Matcher matcher = pattern.matcher(contentType);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                                flag = true;
                            }
                        }
                    }
                    
                    if(!flag) {
                        String htmlWeb = new String(html).toLowerCase();
                        Pattern pattern = Pattern.compile("<meta [^>]*content=\"text/html; charset=([^\"]+)\"");
                        Matcher matcher = pattern.matcher(htmlWeb);
                        if(matcher.find()) {
                            webCharset = matcher.group(1);
                        } else {
                            pattern = Pattern.compile("<meta[^>]*charset=[\"]?([^>]+)[\"]?");
                            matcher = pattern.matcher(htmlWeb);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                            }
                        }
                    }
                    
                    if(webCharset != null) {
                        webCharset = webCharset.replaceAll("/", "").replaceAll("\"", "");
                        if(!webCharset.equals("utf-8") && !webCharset.equals("utf8")) {
                            String tmp = new String(html, webCharset);
                            html = tmp.getBytes(charset);
                        }
                    }
                }
            } else {
                LOG.error(" HttpClient Result Status: " + result + " for url: " + loginUrl);
                try{
                    org.apache.http.Header contentEncodingHeader = response.getLastHeader("Content-Encoding");
                    if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
                        InputStream inStream = entity.getContent();
                        GZIPInputStream gzipStream = new GZIPInputStream(inStream);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int bufSize = 1024;
                        byte [] htmlBuf = new byte[bufSize];
                        int len = 0;
                        while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
                            baos.write(htmlBuf, 0, len);
                        }
                        
                        this.html = baos.toByteArray();
                        this.xmlContent = baos.toString(charset);
                        gzipStream.close();
                        baos.close();
                    } else {
                        this.xmlContent = EntityUtils.toString(entity);
                        this.html = xmlContent.getBytes();
                    }
                }catch(Exception e){
                    
                }
                if(result < 200) {
                    this.errorCode = StatusCode.STATUS_HTTP_INNER_SAVE;
                } else if(result > 200 && result < 300) {
                    this.errorCode = StatusCode.STATUS_HTTP_RES_BROKEN;
                } else if(result < 400) {
                    this.errorCode = StatusCode.STATUS_HTTP_REQ_REDIRECT;
                } else if(result < 500) {
                    this.errorCode = StatusCode.STATUS_HTTP_REQ_FORBIDDEN;
                } else if(result < 600) {
                    this.errorCode = StatusCode.STATUS_HTTP_ROUTE_ERR;
                } else {
                    this.errorCode = StatusCode.STATUS_HTTP_UNDEFIND_ERR;
                }
                this.errorMessage = "Login by httpClient, response status: " + result;
            }
        } catch (IOException e) {
            this.errorCode = StatusCode.STATUS_HTTP_IO_EXCEPTION;
            this.errorMessage = "IO Exception for login by httpClient";
            LOG.error("http Exception: " + loginUrl);
            LOG.error(e.getMessage());
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
//            if (httpPost != null) {
//                httpPost.abort();
//            }
        }
        
        return cookies;
    }
    /**
     * 
     * <一句话功能简述>
     * CookieStore保存的cookie进行网页数据采集
     * <功能详细描述>
     * @author   charles.chen
     * @param url
     * @param rUrl
     * @param cookie
     * @return
     * @see [类、类#方法、类#成员]
     */
    public void downloadPageByGetAndCookieStore(String url,String rUrl,String cookie){
        this.html = null;
        this.xmlContent = null;
        
        HttpGet httpGet = new HttpGet(url);
        if(rUrl != null && rUrl.trim().length() > 0) {
            httpGet.setHeader("Referer", rUrl);
        }
        org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();//(new ThreadSafeClientConnManager());
        HttpResponse response = null;
        HttpUriRequest request = null;
        
        HttpParams params = httpGet.getParams();
        
        params.setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, charset);
        params.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET , charset);
        params.setParameter(HttpMethodParams.HTTP_URI_CHARSET, charset);
        params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connTimeOut);
        params.setParameter(HttpMethodParams.SO_TIMEOUT, socketTimeOut);
        params.setParameter(HttpMethodParams.USER_AGENT, userAgent);
        if(useGzip == 1)
        {
            httpGet.setHeader("Accept-Encoding", "gzip");
        }
        
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        /*
        if(cookie != null && !cookie.isEmpty()){
            String[] cookies = cookie.split(";");
            for(String str : cookies){
                String[] kv = str.trim().split("=");
                if(kv.length > 1){
                    BasicClientCookie baseCookie = new BasicClientCookie(kv[0], kv[1]);
                    cookieStore.addCookie(baseCookie);
                }
            }
        }
        */
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        
        httpGet.setParams(params);
        
        request = httpGet;
        try {
            response = httpClient.execute(request, localContext);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
            LOG.error(e1);
        } catch (IOException e1) {
            e1.printStackTrace();
            LOG.error(e1);
        }
        
       /* HttpResponse response = baiduLoginService.loadPage(url,rUrl);
        */
        if(response == null){
            LOG.error("response is null");
            return ;
        }
        
        getCookieByCookieStore();
        
        try {
            int result = response.getStatusLine().getStatusCode(); 
            HttpEntity entity = response.getEntity();
            this.setHttpStatus(result);
            if (200 == result) {
                org.apache.http.Header contentEncodingHeader = response.getLastHeader("Content-Encoding");//.getResponseHeader("Content-Encoding");
                if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
                    InputStream inStream = entity.getContent();//.getResponseBodyAsStream();
                    GZIPInputStream gzipStream = new GZIPInputStream(inStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufSize = 1024;
                    byte [] htmlBuf = new byte[bufSize];
                    int len = 0;
                    while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
                        baos.write(htmlBuf, 0, len);
                    }
                    
                    this.html = baos.toByteArray();
                    this.xmlContent = baos.toString(charset);
                    
                    gzipStream.close();
                    baos.close();
                } else {
                    this.xmlContent = EntityUtils.toString(entity);
                    this.html = xmlContent.getBytes();
                }
                
                
                //提取webCharset
                if(webCharset == null) {
                    boolean flag = false;
                    org.apache.http.Header charsetTypeHeader = response.getLastHeader("Content-Type");
                    if(charsetTypeHeader != null) {
                        String contentType = charsetTypeHeader.getValue().toLowerCase();
                        if(contentType.indexOf("charset=") > -1) {
                            Pattern pattern = Pattern.compile("charset=([^ ]+)");
                            Matcher matcher = pattern.matcher(contentType);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                                flag = true;
                            }
                        }
                    }
                    
                    if(!flag) {
                        String htmlWeb = new String(html).toLowerCase();
                        Pattern pattern = Pattern.compile("<meta [^>]*content=\"text/html; charset=([^\"]+)\"");
                        Matcher matcher = pattern.matcher(htmlWeb);
                        if(matcher.find()) {
                            webCharset = matcher.group(1);
                        } else {
                            pattern = Pattern.compile("<meta[^>]*charset=[\"]?([^>]+)[\"]?");
                            matcher = pattern.matcher(htmlWeb);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                            }
                        }
                    }
                    
                    if(webCharset != null) {
                        webCharset = webCharset.replaceAll("/", "").replaceAll("\"", "");
                        if(!webCharset.equals("utf-8") && !webCharset.equals("utf8")) {
                            String tmp = new String(html, webCharset);
                            html = tmp.getBytes(charset);
                        }
                    }
                }
            } else {
                LOG.error(" HttpClient Result Status: " + result + " for url: " + url);
                try{
                    org.apache.http.Header contentEncodingHeader = response.getLastHeader("Content-Encoding");
                    if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
                        InputStream inStream = entity.getContent();
                        GZIPInputStream gzipStream = new GZIPInputStream(inStream);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int bufSize = 1024;
                        byte [] htmlBuf = new byte[bufSize];
                        int len = 0;
                        while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
                            baos.write(htmlBuf, 0, len);
                        }
                        
                        this.html = baos.toByteArray();
                        this.xmlContent = baos.toString(charset);
                        gzipStream.close();
                        baos.close();
                    } else {
                        this.xmlContent = EntityUtils.toString(entity);
                        this.html = xmlContent.getBytes();
                    }
                }catch(Exception e){
                    
                }
                if(result < 200) {
                    this.errorCode = StatusCode.STATUS_HTTP_INNER_SAVE;
                } else if(result > 200 && result < 300) {
                    this.errorCode = StatusCode.STATUS_HTTP_RES_BROKEN;
                } else if(result < 400) {
                    this.errorCode = StatusCode.STATUS_HTTP_REQ_REDIRECT;
                } else if(result < 500) {
                    this.errorCode = StatusCode.STATUS_HTTP_REQ_FORBIDDEN;
                } else if(result < 600) {
                    this.errorCode = StatusCode.STATUS_HTTP_ROUTE_ERR;
                } else {
                    this.errorCode = StatusCode.STATUS_HTTP_UNDEFIND_ERR;
                }
                this.errorMessage = "Login by httpClient, response status: " + result;
            }
        } catch (IOException e) {
            this.errorCode = StatusCode.STATUS_HTTP_IO_EXCEPTION;
            this.errorMessage = "IO Exception for loadPage by httpClient";
            LOG.error("http Exception: " + url);
            LOG.error(e.getMessage());
        } finally {
//            if (httpGet != null) {
//                httpGet.abort();
//            }
        }
        
    }
    
    /**
     * 
     * <一句话功能简述>
     * CookieStore保存的cookie进行网页数据采集
     * <功能详细描述>
     * @author   charles.chen
     * @param url
     * @param rUrl
     * @param cookie
     * @return
     * @see [类、类#方法、类#成员]
     */
    public void downloadPageByPostAndCookieStore(String url,String rUrl,String cookie,Map<String, String> postData){
        this.html = null;
        this.xmlContent = null;
        
        org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();//(new ThreadSafeClientConnManager());
        
        
        HttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        
        if(rUrl != null && rUrl.trim().length() > 0) {
            httpPost.setHeader("Referer", rUrl);
        }
    
        LOG.info(url);
        LOG.info(rUrl);
        
        LOG.info("conntimeout: " + connTimeOut);
        LOG.info("so_timeout:" + socketTimeOut);
        
        HttpParams params = new BasicHttpParams(); 
        params.setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, charset);
        params.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET , charset);
        params.setParameter(HttpMethodParams.HTTP_URI_CHARSET, charset);
        params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connTimeOut);
        params.setParameter(HttpMethodParams.SO_TIMEOUT, socketTimeOut);
        params.setParameter(HttpMethodParams.USER_AGENT, userAgent);
        if(useGzip == 1)
        {
            httpPost.setHeader("Accept-Encoding", "gzip");
        }
        
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        httpPost.setParams(params);
        
        HttpEntity postBodyEnt = null;
        try {
            List<org.apache.http.NameValuePair> list = new ArrayList<org.apache.http.NameValuePair>();
            if(postData != null && !postData.isEmpty()) {
                Set<String> keySet = postData.keySet();
                Iterator<String> iter = keySet.iterator();
                while(iter.hasNext()) {
                    String key = iter.next();
                    org.apache.http.NameValuePair nameValuePair = new BasicNameValuePair(key, postData.get(key));
                    list.add(nameValuePair);
                    LOG.info(key+":"+postData.get(key));
                }
            }else{
                LOG.warn("postData is empty");
            }
            postBodyEnt = new UrlEncodedFormEntity(list);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        
        /*if(postBodyEnt == null){
            LOG.error("postBodyEnt is null");
            return ;
        }*/
        
        
        httpPost.setEntity(postBodyEnt);
        
        HttpUriRequest request = httpPost;
        
        HttpContext localContext = new BasicHttpContext();
        
        /*if(cookie != null && !cookie.isEmpty()){
            String[] cookies = cookie.split(";");
            for(String str : cookies){
                String[] kv = str.trim().split("=");
                if(kv.length > 1){
                    BasicClientCookie baseCookie = new BasicClientCookie(kv[0], kv[1]);
                    cookieStore.addCookie(baseCookie);
                }
            }
        }
        */
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        try {
            response = httpClient.execute(request, localContext);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
            LOG.error(e1);
        } catch (IOException e1) {
            e1.printStackTrace();
            LOG.error(e1);
        }
        
        
        
        /*HttpResponse response = baiduLoginService.loadPageByPost(url,rUrl, postData);
        */
        if(response == null){
            LOG.error("response is null");
            return ;
        }
        
        getCookieByCookieStore();

        try {
            int result = response.getStatusLine().getStatusCode(); 
            HttpEntity entity = response.getEntity();
            this.setHttpStatus(result);
            if (200 == result) {
                org.apache.http.Header contentEncodingHeader = response.getLastHeader("Content-Encoding");//.getResponseHeader("Content-Encoding");
                if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
                    InputStream inStream = entity.getContent();//.getResponseBodyAsStream();
                    GZIPInputStream gzipStream = new GZIPInputStream(inStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufSize = 1024;
                    byte [] htmlBuf = new byte[bufSize];
                    int len = 0;
                    while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
                        baos.write(htmlBuf, 0, len);
                    }
                    
                    this.html = baos.toByteArray();
                    this.xmlContent = baos.toString(charset);
                    
                    gzipStream.close();
                    baos.close();
                } else {
                    
                    this.xmlContent = EntityUtils.toString(entity);
                    this.html = xmlContent.getBytes();
                }
                
                
                //提取webCharset
                if(webCharset == null) {
                    boolean flag = false;
                    org.apache.http.Header charsetTypeHeader = response.getLastHeader("Content-Type");
                    if(charsetTypeHeader != null) {
                        String contentType = charsetTypeHeader.getValue().toLowerCase();
                        if(contentType.indexOf("charset=") > -1) {
                            Pattern pattern = Pattern.compile("charset=([^ ]+)");
                            Matcher matcher = pattern.matcher(contentType);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                                flag = true;
                            }
                        }
                    }
                    
                    if(!flag) {
                        String htmlWeb = new String(html).toLowerCase();
                        Pattern pattern = Pattern.compile("<meta [^>]*content=\"text/html; charset=([^\"]+)\"");
                        Matcher matcher = pattern.matcher(htmlWeb);
                        if(matcher.find()) {
                            webCharset = matcher.group(1);
                        } else {
                            pattern = Pattern.compile("<meta[^>]*charset=[\"]?([^>]+)[\"]?");
                            matcher = pattern.matcher(htmlWeb);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                            }
                        }
                    }
                    
                    if(webCharset != null) {
                        webCharset = webCharset.replaceAll("/", "").replaceAll("\"", "");
                        if(!webCharset.equals("utf-8") && !webCharset.equals("utf8")) {
                            String tmp = new String(html, webCharset);
                            html = tmp.getBytes(charset);
                        }
                    }
                }
            } else {
                LOG.error(" HttpClient Result Status: " + result + " for url: " + url);
                try{
                    org.apache.http.Header contentEncodingHeader = response.getLastHeader("Content-Encoding");
                    if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
                        InputStream inStream = entity.getContent();
                        GZIPInputStream gzipStream = new GZIPInputStream(inStream);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int bufSize = 1024;
                        byte [] htmlBuf = new byte[bufSize];
                        int len = 0;
                        while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
                            baos.write(htmlBuf, 0, len);
                        }
                        
                        this.html = baos.toByteArray();
                        this.xmlContent = baos.toString(charset);
                        gzipStream.close();
                        baos.close();
                    } else {
                        this.xmlContent = EntityUtils.toString(entity);
                        this.html = xmlContent.getBytes();
                    }
                }catch(Exception e){
                    
                }
                if(result < 200) {
                    this.errorCode = StatusCode.STATUS_HTTP_INNER_SAVE;
                } else if(result > 200 && result < 300) {
                    this.errorCode = StatusCode.STATUS_HTTP_RES_BROKEN;
                } else if(result < 400) {
                    this.errorCode = StatusCode.STATUS_HTTP_REQ_REDIRECT;
                } else if(result < 500) {
                    this.errorCode = StatusCode.STATUS_HTTP_REQ_FORBIDDEN;
                } else if(result < 600) {
                    this.errorCode = StatusCode.STATUS_HTTP_ROUTE_ERR;
                } else {
                    this.errorCode = StatusCode.STATUS_HTTP_UNDEFIND_ERR;
                }
                this.errorMessage = "Login by httpClient, response status: " + result;
            }
        } catch (IOException e) {
            this.errorCode = StatusCode.STATUS_HTTP_IO_EXCEPTION;
            this.errorMessage = "IO Exception for login by httpClient";
            LOG.error("http Exception: " + url);
            LOG.error(e.getMessage());
        } finally {
//            if (httpPost != null) {
//                httpPost.abort();
//            }
        }
        
    }
    
    /**
     * 
     * <一句话功能简述>
     * 获取重定向URL
     * <功能详细描述>
     * @author   charles.chen
     * @param baseUrl
     * @param rUrl
     * @param cookie
     * @return
     * @see [类、类#方法、类#成员]
     */
    public String getRedirectLocation(String baseUrl, String rUrl, String cookie){
        //从网络下载网页html源码
        String location = null;
        if(useCookieStore == 1)
        {
            location = getUrlLocationByGetAndCookieStore(baseUrl, rUrl, cookie);
        }else{
            location = getUrlLocationByGet(baseUrl, rUrl, cookie);
        }
        //如果是IO异常，则重试两次
        int retryTimes = 0;
        while(errorCode == StatusCode.STATUS_HTTP_IO_EXCEPTION && retryTimes < 2) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ++retryTimes;
            //downPageByGet(baseUrl, rUrl, cookie);
            if(useCookieStore == 1)
            {
                location = getUrlLocationByGetAndCookieStore(baseUrl, rUrl, cookie);
            }else{
                location = getUrlLocationByGet(baseUrl, rUrl, cookie);
            }
        }
        
        /*if(this.errorCode != StatusCode.STATUS_SUCC) {
            LOG.error("Download page by get failed: " + errorCode);
            return location;
        }*/
        
        if(webCharset != null && (invalidTagReplaceList != null || invalidAttrList != null)) {
            processHtmlInvalidTag();
        }
        
        //解析html为Document
        parsePageToDocument();
        if(this.errorCode != StatusCode.STATUS_SUCC) {
            LOG.error("parse page to document failed: " + errorCode);
            return location;
        }
        
        //将Document转换为XML字符串
        convertPageToXml();
        
        processInvalidCharacter();
        
        if(webCharset == null && (invalidTagReplaceList != null || invalidAttrList != null)) {
            processXmlInvalidTag();
        }
        
        return location;
    }

    /**
     * 
     * <一句话功能简述>
     * 获取url跳转链接
     * <功能详细描述>
     * @author   charles.chen
     * @param baseUrl
     * @param rUrl
     * @param cookie
     * @return
     * @see [类、类#方法、类#成员]
     */
    public String getUrlLocationByGet(String baseUrl, String rUrl, String cookie) {
        String location = null;
        GetMethod method = new GetMethod(baseUrl);
        if(rUrl != null && rUrl.trim().length() > 0) {
            method.setRequestHeader("Referer", rUrl);
        }
        if(cookie != null && cookie.trim().length() > 0) {
            method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
            method.setRequestHeader("Cookie", cookie);
        }
        method.getParams().setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, charset);
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET , charset);
        method.getParams().setParameter(HttpMethodParams.HTTP_URI_CHARSET, charset);
        method.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connTimeOut);
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, socketTimeOut);
        method.getParams().setParameter(HttpMethodParams.USER_AGENT, userAgent);
        if(useGzip == 1)
        {
            method.setRequestHeader("Accept-Encoding", "gzip");
        }
        method.setFollowRedirects(false);
        method.getParams().setParameter("http.protocol.allow-circular-redirects", false);
        method.getParams().setParameter("http.protocol.max-redirects", 10);
        try {
            int result = httpClient.executeMethod(method); 
            
            getCookieByGetMethod(method);
            
            this.setHttpStatus(result);
            if (200 == result) {
                Header contentEncodingHeader = method.getResponseHeader("Content-Encoding");
                if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
                    InputStream inStream = method.getResponseBodyAsStream();
                    GZIPInputStream gzipStream = new GZIPInputStream(inStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufSize = 1024;
                    byte [] htmlBuf = new byte[bufSize];
                    int len = 0;
                    while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
                        baos.write(htmlBuf, 0, len);
                    }
                    
                    this.html = baos.toByteArray();
                    this.xmlContent = baos.toString(charset);
                    gzipStream.close();
                    baos.close();
                } else {
                    this.html = method.getResponseBody();
                    this.xmlContent = method.getResponseBodyAsString();
                }
                
                if(webCharset == null) {
                    boolean flag = false;
                    Header charsetTypeHeader = method.getResponseHeader("Content-Type");
                    if(charsetTypeHeader != null) {
                        String contentType = charsetTypeHeader.getValue().toLowerCase();
                        if(contentType.indexOf("charset=") > -1) {
                            Pattern pattern = Pattern.compile("charset=([^ ]+)");
                            Matcher matcher = pattern.matcher(contentType);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                                flag = true;
                            }
                        }
                    }
                    
                    if(!flag) {
                        String htmlWeb = new String(html).toLowerCase();
                        Pattern pattern = Pattern.compile("<meta [^>]*content=\"text/html; charset=([^\"]+)\"");
                        Matcher matcher = pattern.matcher(htmlWeb);
                        if(matcher.find()) {
                            webCharset = matcher.group(1);
                        } else {
                            pattern = Pattern.compile("<meta[^>]*charset=[\"]?([^>]+)[\"]?");
                            matcher = pattern.matcher(htmlWeb);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                            }
                        }
                    }
                    
                    if(webCharset != null) {
                        webCharset = webCharset.replaceAll("/", "").replaceAll("\"", "");
                        LOG.info("webCharset:" + webCharset);
                        if(!webCharset.equals("utf-8") && !webCharset.equals("utf8")) {
                            String tmp = new String(html, webCharset);
                            html = tmp.getBytes(charset);
                        }
                    }
                }
                
                errorCode = StatusCode.STATUS_SUCC;
            } else {
                Header[] headers = method.getResponseHeaders();
                for(Header header:headers){
                    if(header.getName().equals("Location"))
                    {
                        location = header.getValue();
                        LOG.info(header.getName() + ":" + header.getValue());
                    }
                }
                
                LOG.error(" HttpClient Result Status: " + result + " for url: " + baseUrl);
                ungzipByGetMethod(method);
                if(result < 200) {
                    errorCode = StatusCode.STATUS_HTTP_INNER_SAVE;
                } else if(result > 200 && result < 300) {
                    errorCode = StatusCode.STATUS_HTTP_RES_BROKEN;
                } else if(result < 400) {
                    errorCode = StatusCode.STATUS_HTTP_REQ_REDIRECT;
                } else if(result < 500) {
                    errorCode = StatusCode.STATUS_HTTP_REQ_FORBIDDEN;
                } else if(result < 600) {
                    errorCode = StatusCode.STATUS_HTTP_ROUTE_ERR;
                } else {
                    errorCode = StatusCode.STATUS_HTTP_UNDEFIND_ERR;
                }
                errorMessage = "Download page by httpClient with GET method, response status: " + result;
            }

        } catch (IOException e) {
            errorCode = StatusCode.STATUS_HTTP_IO_EXCEPTION;
            errorMessage = "IO Exception for download page by httpClient with GET method; " + e.getMessage();
            LOG.error("http Exception: " + baseUrl);
            LOG.error(e.getMessage());
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
        
        return location;
    }
    
    /**
     * 
     * <一句话功能简述>
     * CookieStore保存的cookie进行网页数据采集
     * <功能详细描述>
     * @author   charles.chen
     * @param url
     * @param rUrl
     * @param cookie
     * @return
     * @see [类、类#方法、类#成员]
     */
    public String getUrlLocationByGetAndCookieStore(String url,String rUrl,String cookie){
        this.html = null;
        this.xmlContent = null;
        String location = null;
        HttpGet httpGet = new HttpGet(url);
        if(rUrl != null && rUrl.trim().length() > 0) {
            httpGet.setHeader("Referer", rUrl);
        }
        org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();//(new ThreadSafeClientConnManager());
        HttpResponse response = null;
        HttpUriRequest request = null;
        
        HttpParams params = httpGet.getParams();
        
        params.setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, charset);
        params.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET , charset);
        params.setParameter(HttpMethodParams.HTTP_URI_CHARSET, charset);
        params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connTimeOut);
        params.setParameter(HttpMethodParams.SO_TIMEOUT, socketTimeOut);
        params.setParameter(HttpMethodParams.USER_AGENT, userAgent);
        if(useGzip == 1)
        {
            httpGet.setHeader("Accept-Encoding", "gzip");
        }
        
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        /*
        if(cookie != null && !cookie.isEmpty()){
            String[] cookies = cookie.split(";");
            for(String str : cookies){
                String[] kv = str.trim().split("=");
                if(kv.length > 1){
                    BasicClientCookie baseCookie = new BasicClientCookie(kv[0], kv[1]);
                    cookieStore.addCookie(baseCookie);
                }
            }
        }
        */
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        
        httpGet.setParams(params);
        
        request = httpGet;
        try {
            response = httpClient.execute(request, localContext);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
            LOG.error(e1);
        } catch (IOException e1) {
            e1.printStackTrace();
            LOG.error(e1);
        }
        
       /* HttpResponse response = baiduLoginService.loadPage(url,rUrl);
        */
        if(response == null){
            LOG.error("response is null");
            return location;
        }
        
        getCookieByCookieStore();
        
        try {
            int result = response.getStatusLine().getStatusCode(); 
            HttpEntity entity = response.getEntity();
            this.setHttpStatus(result);
            if (200 == result) {
                org.apache.http.Header contentEncodingHeader = response.getLastHeader("Content-Encoding");//.getResponseHeader("Content-Encoding");
                if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
                    InputStream inStream = entity.getContent();//.getResponseBodyAsStream();
                    GZIPInputStream gzipStream = new GZIPInputStream(inStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufSize = 1024;
                    byte [] htmlBuf = new byte[bufSize];
                    int len = 0;
                    while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
                        baos.write(htmlBuf, 0, len);
                    }
                    
                    this.html = baos.toByteArray();
                    this.xmlContent = baos.toString(charset);
                    
                    gzipStream.close();
                    baos.close();
                } else {
                    this.xmlContent = EntityUtils.toString(entity);
                    this.html = xmlContent.getBytes();
                }
                
                
                //提取webCharset
                if(webCharset == null) {
                    boolean flag = false;
                    org.apache.http.Header charsetTypeHeader = response.getLastHeader("Content-Type");
                    if(charsetTypeHeader != null) {
                        String contentType = charsetTypeHeader.getValue().toLowerCase();
                        if(contentType.indexOf("charset=") > -1) {
                            Pattern pattern = Pattern.compile("charset=([^ ]+)");
                            Matcher matcher = pattern.matcher(contentType);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                                flag = true;
                            }
                        }
                    }
                    
                    if(!flag) {
                        String htmlWeb = new String(html).toLowerCase();
                        Pattern pattern = Pattern.compile("<meta [^>]*content=\"text/html; charset=([^\"]+)\"");
                        Matcher matcher = pattern.matcher(htmlWeb);
                        if(matcher.find()) {
                            webCharset = matcher.group(1);
                        } else {
                            pattern = Pattern.compile("<meta[^>]*charset=[\"]?([^>]+)[\"]?");
                            matcher = pattern.matcher(htmlWeb);
                            if(matcher.find()) {
                                webCharset = matcher.group(1);
                            }
                        }
                    }
                    
                    if(webCharset != null) {
                        webCharset = webCharset.replaceAll("/", "").replaceAll("\"", "");
                        if(!webCharset.equals("utf-8") && !webCharset.equals("utf8")) {
                            String tmp = new String(html, webCharset);
                            html = tmp.getBytes(charset);
                        }
                    }
                }
            } else {
                
                org.apache.http.Header[] headers = response.getHeaders("Location");
                for(org.apache.http.Header header:headers){
                    if(header.getName().equals("Location"))
                    {
                        location = header.getValue();
                        LOG.info(header.getName() + ":" + header.getValue());
                    }
                }
                
                LOG.error(" HttpClient Result Status: " + result + " for url: " + url);
                try{
                    org.apache.http.Header contentEncodingHeader = response.getLastHeader("Content-Encoding");
                    if(contentEncodingHeader != null && contentEncodingHeader.getValue().toLowerCase().indexOf("gzip") > -1) {
                        InputStream inStream = entity.getContent();
                        GZIPInputStream gzipStream = new GZIPInputStream(inStream);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int bufSize = 1024;
                        byte [] htmlBuf = new byte[bufSize];
                        int len = 0;
                        while((len = gzipStream.read(htmlBuf, 0, bufSize)) != -1) {
                            baos.write(htmlBuf, 0, len);
                        }
                        
                        this.html = baos.toByteArray();
                        this.xmlContent = baos.toString(charset);
                        gzipStream.close();
                        baos.close();
                    } else {
                        this.xmlContent = EntityUtils.toString(entity);
                        this.html = xmlContent.getBytes();
                    }
                }catch(Exception e){
                    
                }
                if(result < 200) {
                    this.errorCode = StatusCode.STATUS_HTTP_INNER_SAVE;
                } else if(result > 200 && result < 300) {
                    this.errorCode = StatusCode.STATUS_HTTP_RES_BROKEN;
                } else if(result < 400) {
                    this.errorCode = StatusCode.STATUS_HTTP_REQ_REDIRECT;
                } else if(result < 500) {
                    this.errorCode = StatusCode.STATUS_HTTP_REQ_FORBIDDEN;
                } else if(result < 600) {
                    this.errorCode = StatusCode.STATUS_HTTP_ROUTE_ERR;
                } else {
                    this.errorCode = StatusCode.STATUS_HTTP_UNDEFIND_ERR;
                }
                this.errorMessage = "Login by httpClient, response status: " + result;
            }
        } catch (IOException e) {
            this.errorCode = StatusCode.STATUS_HTTP_IO_EXCEPTION;
            this.errorMessage = "IO Exception for loadPage by httpClient";
            LOG.error("http Exception: " + url);
            LOG.error(e.getMessage());
        } finally {
//            if (httpGet != null) {
//                httpGet.abort();
//            }
        }
        
        return location;
        
    }
    
    /**
     * 
     * <一句话功能简述>
     * 
     * <功能详细描述>
     * @author   charles.chen
     * @return
     * @see [类、类#方法、类#成员]
     */
    public String getCookie(){
        /*String cookieStr = "";
        if(useCookieStore == 1){
            List<org.apache.http.cookie.Cookie> cookieList = cookieStore.getCookies();
            for(org.apache.http.cookie.Cookie cookie : cookieList){
                cookieStr += (cookie.getName() + "=" + cookie.getValue()) + ";";
            }
        }else{
            Cookie[] cookiesArray = httpClient.getState().getCookies();
            if(cookiesArray != null && cookiesArray.length > 0) {
                for(Cookie c:cookiesArray){
                    cookieStr += c.toString()+";";
                }
            }
        }
        LOG.info("cookieStr:" + cookieStr);*/
        return cookie;
    }
    
    public void getCookieByCookieStore(){
        String cookies = null;
        List<org.apache.http.cookie.Cookie> cookieList = cookieStore.getCookies();
        for(org.apache.http.cookie.Cookie cookie : cookieList){
            cookies += (cookie.getName() + "=" + cookie.getValue()) + ";";
        }
        cookie = cookies;
    }
    
    public void getCookieByPostMethod(PostMethod method){
        //提取 cookies
        String cookies = null;
        Header[] cookieList = method.getResponseHeaders("Set-Cookie");
        if(cookieList != null && cookieList.length > 0)
        {
            for(Header cookie : cookieList) {
                cookies += cookie.getName() + "="+cookie.getValue() + ";";
            }
            //LOG.info("cookies:" + cookies);
        } else {
            Cookie[] cookiesArray = httpClient.getState().getCookies();
            if(cookiesArray != null && cookiesArray.length > 0) {
                for(Cookie c:cookiesArray){
                    cookies += c.toString()+";";
                }
            }
        }
        cookie = cookies;
    }
    
    public void getCookieByGetMethod(GetMethod method){
          //提取 cookies
          String cookies = null;
          Header[] cookieList = method.getResponseHeaders("Set-Cookie");
          if(cookieList != null && cookieList.length > 0)
          {
              for(Header cookie : cookieList) {
                  cookies += cookie.getName() + "="+cookie.getValue() + ";";
              }
              //LOG.info("cookies:" + cookies);
          } else {
              Cookie[] cookiesArray = httpClient.getState().getCookies();
              if(cookiesArray != null && cookiesArray.length > 0) {
                  for(Cookie c:cookiesArray){
                      cookies += c.toString()+";";
                  }
              }
          }
          cookie = cookies;
      }
}
