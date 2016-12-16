package com.cic.datacrawl.runner;

import java.awt.Image;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mozilla.javascript.xml.XMLObject;

import com.cic.datacrawl.core.StatusCode;
import com.cic.datacrawl.core.browser.BackgroundBrowser;
import com.cic.datacrawl.core.browser.JavaWebBrowserImpl;
import com.cic.datacrawl.core.browser.RhinoBrowserImpl;
import com.cic.datacrawl.core.browser.listener.SetURLListener;
import com.cic.datacrawl.core.loginaccount.LoginAccountManager;
import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.crawler.HttpCrawler;
import com.cic.datacrawl.crawler.SnapShotCrawler;

public class DownLoader {
	private static final Logger LOG = Logger.getLogger(DownLoader.class);
	
	private int errorCode;			//错误码
	private int httpStatus;			//网络应答状态码
	private String errorMessage;	//异常或错误消息
	private boolean closeSnapShot;	//网页快照开关（0：使用快照；1：关闭快照——上报、下载都不使用快照）
	private boolean useSnapShot;  	//下载是否使用快照
	private boolean needLogin;  	//是否需要登录
	private byte clientType = 1;  	//下载器类型 1:HttpClient 2:Browser
	private int turnPageWaitTime;	//翻页等待时间
	private String referUrl;		//当前页面url的来源url
	private String url;  			//当前要下载页面的url
	private String htmlContent;  	//当前url下载对应的html文档
	private String xmlContent;  	//当前url下载对应的XML文档
	private String cookie;			//用户设置或从账户管理server获取
	
	private int downPageNum;		//总共下载的页面个数
	
	private Map<String, String> postData;
	
	private RhinoBrowserImpl browser;
	private HttpCrawler httpCrawler;
	private SnapShotCrawler snapShotCrawler;
	private LoginAccountManager accountManager;
	
	public boolean isNeedLogin() {
		return needLogin;
	}

	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}

	public boolean isNeedRender() {
		return clientType == 2;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReferUrl() {
		return referUrl;
	}

	public void setReferUrl(String referUrl) {
		this.referUrl = referUrl;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public int getHttpStatus() {
		return httpStatus;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public boolean isCloseSnapShot() {
		return closeSnapShot;
	}

	public void setCloseSnapShot(boolean closeSnapShot) {
		this.closeSnapShot = closeSnapShot;
	}
	
	public boolean isUseSnapShot() {
		return useSnapShot;
	}

	public byte getClientType() {
		return clientType;
	}

	public int getTurnPageWaitTime() {
		return turnPageWaitTime;
	}

	public void setTurnPageWaitTime(int turnPageWaitTime) {
		this.turnPageWaitTime = turnPageWaitTime;
	}


	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public String getXmlContent() {
		return xmlContent;
	}

	public int getDownPageNum() {
		return downPageNum;
	}

	public void setDownPageNum(int downPageNum) {
		this.downPageNum = downPageNum;
	}

	public String getUserName() {
		return accountManager.getUserName();
	}
	
	public String getPasswd() {
		return accountManager.getPasswd();
	}
	
	public boolean needLoginCheck(){
		return accountManager.isAutoLogin();
	}
	
	public void setLoginUrl(String loginUrl) {
		accountManager.setLoginUrl(loginUrl);
	}
	
	public void setUserNameKey(String userNameKey) {
		accountManager.setUserNameKey(userNameKey);
	}
	
	public void setPasswdKey(String passwdKey) {
		accountManager.setPasswdKey(passwdKey);
	}
	//用于登录的post数据，避免需要调用POST方法的调用覆盖post的数据
	public void addLoginPostData(String key, String value) {
		accountManager.addPostData(key, value);
	}
	//调用POST方法的post数据
	public void addPostData(String key, String value) {
		if(postData == null) {
			postData = new HashMap<String, String>();
		}
		postData.put(key, value);
	}
	//调用POST方法的下载逻辑，想设置post数据前，要先clear老的post数据
	public void clearPostData() {
		if(postData != null)
		{
			postData.clear();
		}
	}
	
	//调用POST方法的下载逻辑，想设置post数据前，要先clear老的post数据
	public void clearLoginPostData() {
		if(accountManager.getPostData() != null)
		{
			accountManager.getPostData().clear();
		}
	}
	
	//在TaskRunner配置文件中配置SnapShot及账号管理系统的 监听IP:PORT
	public void init(byte clientType, boolean closeSnapShot, boolean useSnapShot, int siteId, int agentSubgroupId, String snapShotIp, int snapShotPort, String loginAccountIp, int loginAccountPort, int turnPageWaitTime) {
		this.downPageNum = 0;
		this.clientType = clientType;
		this.closeSnapShot = closeSnapShot;
		this.useSnapShot = useSnapShot;
		this.turnPageWaitTime = turnPageWaitTime;
		accountManager = new LoginAccountManager();
		accountManager.init(siteId, agentSubgroupId, loginAccountIp, loginAccountPort);
		if(!closeSnapShot) {
			snapShotCrawler = new SnapShotCrawler();
//			snapShotCrawler.init(siteId, snapShotIp, snapShotPort);
		}
		
		if(clientType == 1) {
			accountManager.setShareAccount(true);
			if(httpCrawler == null){
				LOG.info("init the httpCrawler..");
				httpCrawler = new HttpCrawler();
				httpCrawler.init();
			}
		} else {
		    if(browser == null){
		        accountManager.setShareAccount(false);
	            JavaWebBrowserImpl javaBrowser = new BackgroundBrowser().getWebBrowser();
	            javaBrowser.initForRobotEditor(null);
	            browser = RhinoBrowserImpl.newInstance(javaBrowser);
	            browser.registerSetURLListener(new SetURLListener() {
	                private DeadCycleDetecter detecter = DeadCycleDetecter.getInstance();
	                @Override
	                public void doBeforeUrlLocationChanged(String url) throws Exception {
	                    detecter.visit(url);
	                }
	                @Override
	                public void doAfterUrlLocationChanged(String url) throws Exception {
	                }
	            });
		    }
		}
	}
	
	public void clear() {
		accountManager.closeNetWork();
//		if(!closeSnapShot) {
//			snapShotCrawler.closeSnapShot();
//		}
	}
	
	//Login httpClient的登录逻辑
	public boolean login() {
		if(!accountManager.allocateAccount()) {
			errorCode = StatusCode.STATUS_LOGIN_ALLOC_ACCOUNT_FAILED;
			errorMessage = "Allocate login account failed";
			return false;
		}
		
		if(clientType == 1) {
			// 利用账号密码进行自动登录
			if(accountManager.isAutoLogin()){
				this.downPageNum++;
				LOG.info(accountManager.getPostData());
				cookie = httpCrawler.login(accountManager.getLoginUrl(), referUrl, accountManager.getPostData());
				LOG.info("get cookie:" + cookie);
				
				httpStatus = httpCrawler.getHttpStatus();
				errorCode = httpCrawler.getErrorCode();
				errorMessage = httpCrawler.getErrorMessage();
				
				htmlContent = httpCrawler.getHtmlContent();
				xmlContent = httpCrawler.getXmlContent();
				
				if(cookie == null) {
					accountManager.setStatus(3); //登录失败
					return false;
				} else {
					accountManager.setStatus(1);  //获取cookie成功
					accountManager.setCookie(cookie);
				}
			}
		} else if(clientType == 2) {
			Map<String, String> postData = accountManager.getPostData();
			String url = accountManager.getLoginUrl();
			if(postData != null && postData.size() > 0){
				// 不包含参数
				if(!url.contains("?")){
					url += "?";
				}else{
					url += "&";
				}
				
				Set<String> keySet = postData.keySet();
				Iterator<String> iter = keySet.iterator();
				while(iter.hasNext()) {
					String key = iter.next();
					url += key + "=" + postData.get(key) + "&";
				}
				// 去掉最后一个&/?
				url = url.substring(0, url.length() - 1);
			}
			LOG.info("url:" + url);
			browser.setUrl(url);
			httpStatus = browser.getHttpStatus();
			// 加载页面成功返回0, 加载失败返回相应的错误号
			errorCode = browser.getErrorCode();
			
			if(errorCode > 0){
				return false;
			}
		}
		
		return true;
	}
	
		
	public void accountFailed(){
		accountManager.accountFailed();
	}
	
	public void loginFailed() {
		if(this.clientType == 1) {
			accountManager.setLoginFailed();
		} else if(this.clientType == 2) {
			browser.clearCookies();
		}
	}
	
	public void loginSuccess() {
		if(clientType == 1) {
			accountManager.saveCookie();
		}
	}
	
	//默认使用GET方式下载
	public void loadPage() {
		this.downPageNum++;
		
		if(!isCloseSnapShot() && isUseSnapShot()) {
			snapShotCrawler.downLoadPage(url);
			errorCode = snapShotCrawler.getErrorCode();
			if(errorCode != StatusCode.STATUS_SNAPSHOT_NOT_EXIST) {
				httpStatus = 0;
				htmlContent = snapShotCrawler.getHtmlContent();
				xmlContent = snapShotCrawler.getXmlContent();
				return;
			}
		}
		
		if(clientType == 1) {
			LOG.debug("isNeedLogin: " + isNeedLogin());
			if(isNeedLogin()) {
				cookie = accountManager.getCookie();
			}
			
			httpCrawler.downloadPage(url, referUrl, cookie);
			httpStatus = httpCrawler.getHttpStatus();
			errorCode = httpCrawler.getErrorCode();
			errorMessage = httpCrawler.getErrorMessage();
			LOG.debug("httpCrawler errorCode: " + errorCode);
			htmlContent = httpCrawler.getHtmlContent();
			xmlContent = httpCrawler.getXmlContent();
			if(!isCloseSnapShot()) {
				snapShotCrawler.saveSnapShot(url, clientType, htmlContent);
			}
		} else if (clientType == 2) {
			browser.setUrl(url);
			httpStatus = browser.getHttpStatus();
			errorCode = browser.getErrorCode();
			htmlContent = browser.getHtmlContent();
			xmlContent = browser.getXmlContent();
//			htmlContent = browser.getDocument().getHtmlContent();
//			xmlContent = browser.getDocument().getXmlContent();
			if(!isCloseSnapShot()) {
				snapShotCrawler.saveSnapShot(url, clientType, xmlContent);
			}
		}
	}

	//使用post方法采集的站点禁止使用网页快照功能：快照使用url作为key，而post方法的url包含信息部完整，会导致大量url重复
	//browser采集方式也不会调用该方法 
	public void loadPageByPost() {
		if(clientType == 2) {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
			return;
		}
		
		this.downPageNum++;
				
		//String cookie = null;
		LOG.debug("isNeedLogin: " + isNeedLogin());
		if(isNeedLogin()) {
			cookie = accountManager.getCookie();
		}
		
		httpCrawler.downloadPage(url, referUrl, cookie, postData);
		httpStatus = httpCrawler.getHttpStatus();
		errorCode = httpCrawler.getErrorCode();
		errorMessage = httpCrawler.getErrorMessage();
		LOG.debug("httpCrawler errorCode: " + errorCode);
		htmlContent = httpCrawler.getHtmlContent();
		xmlContent = httpCrawler.getXmlContent();
		if(!isCloseSnapShot()) {
			snapShotCrawler.saveSnapShot(url, clientType, htmlContent);
		}
		
		//清理掉post数据，每个页面使用一组post数据
		clearPostData();
	}
	
	public void jsExecute(String jsStr) {
		if(clientType == 2) {
			this.downPageNum++;
			browser.jsExecute(jsStr);
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}
	
	public void elementClick(XMLObject xmlObject) {
		if(clientType == 2) {
			this.downPageNum++;
			browser.elementClick(xmlObject);
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}
	
	public void elementSubmit(XMLObject xmlObject) {
		if(clientType == 2) {
			this.downPageNum++;
			browser.elementSubmit(xmlObject);
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void elementSetChecked(XMLObject xmlObject, boolean bChecked) {
		if(clientType == 2) {
			browser.elementSetChecked(xmlObject, bChecked);
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void elementSetValue(XMLObject xmlObject, String value) {
		if(clientType == 2) {
			browser.elementSetValue(xmlObject, value);
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}
	
	public Image readImageData(String imgElementId) {
		if(clientType == 2) {
			return browser.readImageData(imgElementId);
		}  else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
			return null;
		}
	}
	
	public void back() {
		if(clientType == 2) {
			browser.back();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void forward() {
		if(clientType == 2) {
			browser.forward();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}
	
	public String getDomain() {
		String url = getUrl().trim();

		if (StringUtil.isEmpty(url)) {
			return "";
		}
		if (url.toLowerCase().startsWith("http://")) {
			return url.substring(7, url.indexOf("/", 8));
		} else if(url.toLowerCase().startsWith("https://")){
			return url.substring(8, url.indexOf("/", 9));
		}else {
			return url.substring(0, url.indexOf("/"));
		}
	}
	
	public String getUrlPath() {
		String url = this.url;

		if (StringUtil.isEmpty(url)) {
			return "";
		}

		return url.substring(0, url.trim().lastIndexOf("/"));
	}
	
	public void setTimeOut(int timeOut) {
		if(clientType == 1) {
			httpCrawler.setConnTimeOut(timeOut);
			httpCrawler.setSocketTimeOut(timeOut);
		} else {
			browser.setTimeout(timeOut);
		}
	}
	
	public long getTimeOut() {
		if(clientType == 1) {
			return httpCrawler.getConnTimeOut();
		} else if(clientType == 2) {
			return browser.getTimeout();
		} else {
			return 0;
		}
	}

	public void disableFlash() {
		if(clientType == 2) {
			browser.disableFlash();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void disableImage() {
		if(clientType == 2) {
			browser.disableImage();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void disableIFrame() {
		if(clientType == 2) {
			browser.disableIFrame();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void disableJavascript() {
		if(clientType == 2) {
			browser.disableJavascript();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void enableIFrame() {
		if(clientType == 2) {
			browser.enableIFrame();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void enableFlash() {
		if(clientType == 2) {
			browser.enableFlash();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void enableImage() {
		if(clientType == 2) {
			browser.enableImage();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void enableJavascript() {
		if(clientType == 2) {
			browser.enableJavascript();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}
	
	public void disableCache() {
		if(clientType == 2) {
			browser.disableCache();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public void enableCache() {
		if(clientType == 2) {
			browser.enableCache();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
		}
	}

	public int refresh() {
		if(clientType == 2) {
			return browser.refresh();
		} else {
			errorCode = StatusCode.STATUS_INVALID_CLIENT_TYPE;
			errorMessage = "httpClient Cannot exec Interface for Browser";
			return errorCode;
		}
	}
	
	public void addIgnoreURLPattern(Object urlPattern) {
		if(clientType == 2) {
			browser.addIgnoreURLPattern(urlPattern);
		}
	}
	
	public void clearIgnoreURLPattern() {
		if(clientType == 2) {
			browser.clearIgnoreURLPattern();
		}
	}
	
	public void clearCookies() {
		if(clientType == 2) {
			browser.clearCookies();
		}
	}
	
	public void setWebCharset(String charset) {
		if(clientType == 1) {
			httpCrawler.setWebCharset(charset);
		}
	}
	
	public void setXmlCharset(String charset) {
		if(clientType == 1) {
			if(charset == null){
				httpCrawler.setXmlCharset(httpCrawler.getWebCharset());
			}else{
				httpCrawler.setXmlCharset(charset);
			}
		}
	}
	
	public String getWebCharset() {
		return httpCrawler.getWebCharset();
	}
	
	public void addInvalidTagReplacePair(String key, String value) {
		if(clientType == 1) {
			httpCrawler.addInvalidTagReplacePair(key, value);
		}
	}
	
	public void addInvalidArrt(String attr) {
		if(clientType == 1) {
			httpCrawler.addInvalidArrt(attr);
		}
	}
	
	public void setUserAgent(String userAgent) {
	    if(clientType == 1) {
	        httpCrawler.setUserAgent(userAgent);
	    }
    }
	
	public void setUseGzip(int useGzip) {
	    if(clientType == 1) {
	        httpCrawler.setUseGzip(useGzip);
	    }
    }
	
	public void setUseCookieStore(int useCookieStore) {
        if(clientType == 1) {
            httpCrawler.setUseCookieStore(useCookieStore);
        }
    }
	
	public String getCookie() {
	    if(clientType == 1) {
	        return httpCrawler.getCookie();
	    }else{
	        String cookieStr = "";
	        com.cic.datacrawl.core.browser.entity.Cookie[] cookiesArray = browser.getCookies();
	        if(cookiesArray != null && cookiesArray.length > 0) {
                for(com.cic.datacrawl.core.browser.entity.Cookie c:cookiesArray){
                    cookieStr += c.getName() + "="+c.getValue() + ";";
                }
            }
	        return cookieStr;
	    }
    }
	
	public String getRedirectLocation(String baseUrl, String rUrl, String cookie){
	    String location = null;
	    if(clientType == 1) {
	        location = httpCrawler.getRedirectLocation(baseUrl, rUrl, cookie);
            httpStatus = httpCrawler.getHttpStatus();
            errorCode = httpCrawler.getErrorCode();
            errorMessage = httpCrawler.getErrorMessage();
            LOG.debug("httpCrawler errorCode: " + errorCode);
            htmlContent = httpCrawler.getHtmlContent();
            xmlContent = httpCrawler.getXmlContent();
            if(!isCloseSnapShot()) {
                snapShotCrawler.saveSnapShot(url, clientType, htmlContent);
            }
        }else{
            //LOG.error("can not get location by browser!");
            browser.setUrl(baseUrl);
            location = browser.getUrl();
        }
	    return location;
	}
	
	
	public void initHttpClient(){
	    if(httpCrawler == null){
            httpCrawler = new HttpCrawler();
            httpCrawler.init();
        }else{
            LOG.info("init the httpClient..");
            httpCrawler = new HttpCrawler();
            httpCrawler.init();
        }
	}
	
	public String requestCookie(){
	    if(accountManager != null){
	        if(!accountManager.allocateAccount()) {
	            return accountManager.getCookie();
	        }
	    }
	    return null;
	}
	
	public void returnAccount(){
	    if(accountManager != null)
	    {
	        accountManager.returnAccount();
	    }
	}

    public void setClientType(byte clientType) {
        this.clientType = clientType;
        if(clientType == 1) {
            accountManager.setShareAccount(true);
            if(httpCrawler == null){
                LOG.info("init the httpCrawler..");
                httpCrawler = new HttpCrawler();
                httpCrawler.init();
            }
        } else {
            accountManager.setShareAccount(false);
            if(browser == null){
                JavaWebBrowserImpl javaBrowser = new BackgroundBrowser().getWebBrowser();
                javaBrowser.initForRobotEditor(null);
                browser = RhinoBrowserImpl.newInstance(javaBrowser);
                browser.registerSetURLListener(new SetURLListener() {
                    private DeadCycleDetecter detecter = DeadCycleDetecter.getInstance();
                    @Override
                    public void doBeforeUrlLocationChanged(String url) throws Exception {
                        detecter.visit(url);
                    }
                    @Override
                    public void doAfterUrlLocationChanged(String url) throws Exception {
                    }
                });
            }
        }
    }
	
	
}
