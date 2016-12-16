package com.cic.datacrawl.core.browser.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.cic.datacrawl.core.browser.entity.Cookie;

public class RhinoHttpClient {
  public RhinoHttpClient(Cookie[] cookies) {
    httpclient = new DefaultHttpClient();
    localContext = new BasicHttpContext();

    httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
    BasicCookieStore cookieStore = new BasicCookieStore();
    for (int i=0; i < cookies.length; i++) {
      Cookie jcookie = cookies[i];
      BasicClientCookie bcookie = new BasicClientCookie(jcookie.getName(), jcookie.getValue());
      bcookie.setVersion(0);
      bcookie.setDomain(jcookie.getDomain());
      bcookie.setPath(jcookie.getPath());
      bcookie.setExpiryDate(jcookie.getExpiryDate());
      cookieStore.addCookie(bcookie);
    }
    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
  }
  
  private HttpClient httpclient;
  private HttpContext localContext;
  
  private HttpGet httpget;
  private HttpResponse httpResponse;
  
  // 执行http GET请求
  public void httpGet(String url) {
    if (httpResponse != null) {
      HttpEntity httpEntity = httpResponse.getEntity();
      try {
        if (httpEntity != null)
          httpEntity.consumeContent();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      httpResponse = null;
    }
    
    HttpGet httpget = new HttpGet(url); 
    try {
      httpResponse = httpclient.execute(httpget, localContext);
    } catch (ClientProtocolException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  // 获取http应答号
  public int getRespStatus() {
    return httpResponse.getStatusLine().getStatusCode();
  }
  
  // 获取http内容类型
  public String getContentType() {
    Header header = httpResponse.getFirstHeader("Content-Type");
    if (header == null) return null;
    
    return header.getValue();
  }
  
  // 获取文本内容(HTML或XML文件内容)
  public String getStringContent(String charset) {
    HttpEntity httpEntity = httpResponse.getEntity();
    if (httpEntity == null) return null;

    if (charset == null) charset = "GBK";
    try {
      return new String(EntityUtils.toByteArray(httpEntity), charset);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  // 获取二进制内容
  public byte[] getBinaryContent() {
    HttpEntity httpEntity = httpResponse.getEntity();
    if (httpEntity == null) return null;

    try {
      return EntityUtils.toByteArray(httpEntity);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  // 获取下载文件的文件名
  public String getContentDisposition(String charset) {
    Header header = httpResponse.getFirstHeader("Content-Disposition");
    if (header == null) return null;
    
    if (charset == null) charset = "GBK";
    String ts = header.getValue();
    try {
      ts = new String(ts.getBytes(HTTP.DEFAULT_CONTENT_CHARSET), charset);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    if (ts.startsWith("attachment; filename="))
      ts = ts.substring("attachment; filename=".length());
    return ts;
  }
}
