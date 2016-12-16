package com.cic.datacrawl.crawler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class RefererCrawler {
	private String baseUrl;
	private String rUrl;
	private String content;
	
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getrUrl() {
		return rUrl;
	}
	public void setrUrl(String rUrl) {
		this.rUrl = rUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setUrl(String baseUrl, String rUrl) {
		setBaseUrl(baseUrl);
		setrUrl(rUrl);
	}
	
	public void loadPage(){
		String charset = "UTF-8";
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(getBaseUrl());
 
		getMethod.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36");
        getMethod.getParams().setParameter(HttpMethodParams.HTTP_URI_CHARSET, charset);
        getMethod.getParams().setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, charset);
        getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET , charset);
        getMethod.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5000);
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 6000);
        getMethod.setRequestHeader("Referer", getrUrl());
        getMethod.setFollowRedirects(true);
        try {
            //设置 HttpClient 接收 Cookie,用与浏览器一样的策略
            int result = httpClient.executeMethod(getMethod);
            if(result == 200) {
            	String content = getMethod.getResponseBodyAsString();
            	setContent(content.trim());
            } else {
            	setContent(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setContent(null);
        }   
	}
}
