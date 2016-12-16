package com.cic.datacrawl.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.browser.BackgroundBrowser;
import com.cic.datacrawl.core.browser.JavaWebBrowserImpl;
import com.cic.datacrawl.core.browser.RhinoBrowserImpl;
import com.cic.datacrawl.core.config.Config;

public class BrowserTest {
	private static final Logger LOG = Logger.getLogger(BrowserTest.class);
	
	public static void main(String[] args) {
		String path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
		ApplicationContext.initialiaze(path, true);
		
		String baseUrl = "http://item.taobao.com/item.htm?spm=a1z10.1.w5003-3581698514.3.SARiDr&id=26263188157&scene=taobao_shop";
 		String html = null;//getHttp(baseUrl);
		String xml = htmlToXml(html, baseUrl, "UTF-8");
		
		File file = new File("E:\\output_browser.html");
		
		Writer out = null;
		try {
			FileOutputStream s = new FileOutputStream(file);
			OutputStreamWriter w = new OutputStreamWriter(s, "UTF-8");
			PrintWriter pw = new PrintWriter(w);
			pw.println(xml);
			
			pw.flush();
			pw.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		
		LOG.info("Output success");
		
		System.exit(0);
	}

	public static String readToString(String fileName) {
		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new String(filecontent);
	}
	
	public static String getHttp(String url) {
		HttpClient client = null;
		String text = null;
		LOG.info(url.length() + "--> " + url);
		HttpMethod method = new GetMethod(url);		
		method.getParams().setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, "UTF-8"); 
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		method.setFollowRedirects(false);
		try {
			client = new HttpClient();
			int result = client.executeMethod(method);
			LOG.info("res:" + result);
			
			if (200 == result) {
				text = method.getResponseBodyAsString();
			} else {
				LOG.error(" ----------------------------");
				return null;
			}

		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		
		return text;
	}
	
	public static String htmlToXml(String html, String baseUrl, String charset) {
	/**
	 * Browser 模式
	 */
		try {
			LOG.info("Start Browser to parse html");
 			JavaWebBrowserImpl javaBrowser = new BackgroundBrowser().getWebBrowser();
			javaBrowser.initForRobotEditor(null);
			RhinoBrowserImpl browser = RhinoBrowserImpl.newInstance(javaBrowser);
			LOG.info("New Browser Instance success");
			
			browser.setCharset("utf-8");
			browser.setUrl(baseUrl);
			System.out.println(System.currentTimeMillis());
			String xml = browser.getDocument().getXmlContent();
			System.out.println(System.currentTimeMillis());
			html = browser.getXmlContent();
			return xml;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
		
		return null;
		
	}
}
