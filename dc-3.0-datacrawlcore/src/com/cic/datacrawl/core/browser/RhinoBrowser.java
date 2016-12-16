package com.cic.datacrawl.core.browser;

import org.mozilla.javascript.xml.XMLObject;

import com.cic.datacrawl.core.browser.entity.BrowserDocument;
import com.cic.datacrawl.core.browser.entity.Cookie;
import com.cic.datacrawl.core.browser.tools.RhinoHttpClient;

/**
 * 供Rhino访问的浏览器接口
 */
public interface RhinoBrowser {

	void loadHtml(String html, String baseUrl); // testing

	/**
	 * 添加Cookie
	 * 
	 * @param cookie
	 */
	void addCookie(String cookie);

	void addCookie(Cookie cookie);

	void addCookie(Cookie[] cookies);

	void clearCookies();

	/**
	 * 获得当前的Cookie
	 * 
	 * @return
	 */
	Cookie[] getCookies();

	void setCharset(String charset);

	/**
	 * 浏览器的后退功能
	 */
	void back();

	/**
	 * 浏览器的前进功能
	 */
	void forward();

	public String getDomain();

	public String getUrlPath();

	/**
	 * 添加一个ContentFilter
	 * 
	 * @param regrex
	 * @param replaceTo
	 */
	void addContentReplacement(String regrex, String replaceTo);

	/**
	 * 删除一个ContentFilter
	 * 
	 * @param name
	 * @param regrex
	 * @param replaceTo
	 */
	void removeContentReplacement(String name);

	/**
	 * 删除所有的ContentFilter
	 * 
	 * @param name
	 * @param regrex
	 * @param replaceTo
	 */
	void removeAllContentReplacement();

	/**
	 * 加载指定页面, 成功返回0，连接失败返回相应的HTTP STATUS错误号，超时返回901，无法访问网站返回902
	 * 
	 * @param url
	 */
	int setUrl(String url);

	/**
	 * 等待加载完成. 成功返回0, 失败返回相应的错误号
	 * 
	 * @return
	 */
	int getErrorCode();

	/**
	 * 获取应答状态号
	 * 
	 * @return
	 */
	int getHttpStatus();

	/**
	 * 获取cookie,并初始化一个含相应cookie的HttpClient
	 * 
	 * @return
	 */
	RhinoHttpClient getCookiedHttpClient();

	/**
	 * 返回当前页面的url 因redirect,refresh等情况的存在，当前页面与之前setUrl指定的可能并不一致
	 * 
	 * @return
	 */
	String getUrl();

	/**
	 * 获取网页内容, 可能不符合XML规范
	 * 
	 * @return
	 */
	String getHtmlContent();

	BrowserDocument getDocument();

	/**
	 * 获取符合XML规范的网页内容
	 * 
	 * @return
	 */
	String getXmlContent();

	/**
	 * 执行javascript脚本
	 * 
	 * @param script
	 */
	void jsExecute(String script);

	/**
	 * 点击页面上的指定元素
	 * 
	 * @param xmlObject
	 *            通过E4X计算得到的XML元素
	 */
	void elementClick(XMLObject xmlObject);

	// /**
	// * 设置页面上列表项的选择状态
	// *
	// * @param xmlObject
	// * 通过E4X计算得到的XML元素
	// */
	// public void elementSetSelected(XMLObject xmlObject, boolean selected);
	/**
	 * 页面form提交
	 * 
	 * @param xmlObject
	 */
	void elementSubmit(XMLObject xmlObject);

	/**
	 * 设置页面上的指定元素的值
	 * 
	 * @param xmlObject
	 *            通过E4X计算得到的XML元素
	 */
	void elementSetValue(XMLObject xmlObject, String value);

	/**
	 * 设置页面上的单选，复选框是否选中
	 * 
	 * @param xmlObject
	 *            通过E4X计算得到的XML元素
	 */
	void elementSetChecked(final XMLObject xmlObject, boolean bChecked);

	/**
	 * 禁用/启用 浏览器的Flash功能
	 */
	public void disableFlash();

	public void disableCache();

	/**
	 * 禁用 浏览器的图片功能
	 */
	public void disableImage();

	/**
	 * 禁用 浏览器的javascript功能
	 */
	public void disableJavascript();

	/**
	 * 禁用 浏览器的IFrame功能
	 */
	public void disableIFrame();

	/**
	 * 启用 浏览器的IFrame功能
	 */
	public void enableIFrame();

	public void enableCache();

	/**
	 * 启用 浏览器的Flash功能
	 */
	public void enableFlash();

	/**
	 * 启用 浏览器的图片功能
	 */
	public void enableImage();

	/**
	 * 启用 浏览器的javascript功能
	 */
	public void enableJavascript();

	/**
	 * 将cache中的图片另存为文件
	 * 
	 * @param imgElementId
	 * @param fileName
	 */
	java.awt.Image readImageData(String imgElementId);

	/**
	 * 利用WebBrowser进行文件下载
	 * 
	 * @param imageURI
	 * @param fileName
	 */
	void downloadFile(String imageURI, String fileName);

	/**
	 * 在浏览器中显示指定的html
	 * 
	 * @param html
	 */
	void setHtmlSourceCode(String html);

	/**
	 * 设置网页的超时时间
	 * 
	 * @param timeout
	 */
	void setTimeout(long timeout);

	/**
	 * 设置网页的超时时间
	 * 
	 * @param timeout
	 */
	long getTimeout();

	/**
	 * 刷新页面
	 */
	// void refresh();
	int refresh();// added by steven

	void addIgnoreURLPattern(Object urlPattern);
	
	public void clearIgnoreURLPattern();

	/**
	 * 增加URL匹配的白名单，符合白名单的url将一定作为判断网站是否加载完成的依据
	 * 
	 * @param urlPattern
	 */
//	void addValidURLPattern(Object urlPattern);

//	public static final int MONITOR_TYPE_DEFAULT = AbstractJavaWebBrowser.MONITOR_TYPE_DEFAULT;
//	public static final int MONITOR_TYPE_ONLY_ALLOW_WHITE_LIST = AbstractJavaWebBrowser.MONITOR_TYPE_ONLY_ALLOW_WHITE_LIST;
//
//	void setURLMonitorType(int type);
//
//	int getURLMonitorType();
	

//	public void clearValidURLPattern();
}
