package com.cic.datacrawl.core.loginaccount;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cic.datacrawl.account.rpc.protocol.ResTaskRunnerReqAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerReqAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerReturnAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerUpdateAccountCookieWritable;
import com.cic.datacrawl.core.rpc.ClientImpl;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ServiceName;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;


/**
 * 与LoginAccountService交互：获取账号/Cookie、更新Cookie、失效则更换账号、归还账号
 * 记录账号登录的基本信息
 * 登陆共享仅适用于HttpClient 
 * @author johnney.bu
 */

public class LoginAccountManager {
	private static final Logger LOG = Logger.getLogger(LoginAccountManager.class);
	
	private int errorCode;				//错误码
	private boolean isAutoLogin;		//是否自动登录
	private boolean shareAccount;  		//是否共享账号，告诉账号管理Server分配的账号是否被用于共享
	private int siteId;  				//网站ID，用于分配该站点的登录账号
	private int agentSubgroupId;  		//子网分组ID，用于相同网络出口的cookie共享
	private String loginUrl;			//网站登录url
	private int accountId;				//分配的账号ID
	private int status;					//账号状态 1-账号有效 2-cookie失效 3-账号登录失败
	private String userName;			//登录用户名
	private String passwd;				//登录密码
	private String cookie;				//登录后获取的cookie，或者直接从账号管理Server分配到的cookie
	
	private ClientImpl rpcClient;		//连接账号管理Server的客户端
	
	//HttpClient 登录需要的参数
	private Map<String, String> postData = new HashMap<String, String>();	//登录使用的Post信息
	private String userNameKey;  			//登录中标识用户名的Key
	private String passwdKey;  				//登录中标识密码的Key
	
	//Browser 登录需要两次交互：下载loginUrl，然后定位到用户名、密码输入框行登录按钮，实现登录
	//该逻辑需要使用E4X定位，要放到login.js中实现该逻辑

	public LoginAccountManager() {
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public boolean isAutoLogin() {
		return isAutoLogin;
	}

	public void setAutoLogin(boolean isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
	}
	
	public boolean isShareAccount() {
		return shareAccount;
	}

	public void setShareAccount(boolean shareAccount) {
		this.shareAccount = shareAccount;
//		if(shareAccount && postData == null) {
//			postData = new HashMap<String, String>();
//		}
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getAgentSubgroupId() {
		return agentSubgroupId;
	}

	public void setAgentSubgroupId(int agentSubgroupId) {
		this.agentSubgroupId = agentSubgroupId;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public Map<String, String> getPostData() {
		return postData;
	}

	public void setPostData(Map<String, String> postData) {
		this.postData = postData;
	}

	public String getUserNameKey() {
		return userNameKey;
	}

	public void setUserNameKey(String userNameKey) {
		this.userNameKey = userNameKey;
	}

	public String getPasswdKey() {
		return passwdKey;
	}

	public void setPasswdKey(String passwdKey) {
		this.passwdKey = passwdKey;
	}
	
	public void addPostData(String key, String value) {
		this.postData.put(key, value);
	}
	
	public void init(int siteId, int agentSubgroupId, String loginAccountIp, int loginAccountPort) {
		this.accountId = 0;
		this.siteId = siteId;
		this.agentSubgroupId = agentSubgroupId;
		this.status = 1;
		this.rpcClient = new ClientImpl(loginAccountIp, loginAccountPort);
	}
	
	public void closeNetWork() {
		if(this.accountId > 0){
			//先归还账号
			returnAccount();
		}
		this.rpcClient.close();
	}
	
	//分配账号
	public boolean allocateAccount() {
		boolean flag = true;
		flag = requestAllocateAccount();
		if(flag){
			//判断分配的账号是否有效
			if(isAutoLogin() && (cookie==null || cookie.isEmpty())){
				if(userName==null || userName.isEmpty() || passwd==null || passwd.isEmpty()){
					LOG.error("Allocate Account Failed: username or passwd is empty");
					flag = false;
				}else{
					postData.put(userNameKey, userName);
					postData.put(passwdKey, passwd);
				}
			} else if(cookie==null){
				LOG.error("Allocate Account Failed: cookie is null");
				flag = false;
			}
		}
		return flag;
	}
		
	//通知server，当前账号登录失败
	public void setLoginFailed() {
		if(userName!=null && !userName.isEmpty() && passwd!=null && !passwd.isEmpty()){
			status = 3;
		} else {
			status = 2;
		}
	}
	
	public void accountFailed(){
		status = 2;
//		returnAccount();
	}
	
	//如果是账号登录，则要吧cookie上传给server
	public int saveCookie() {
		int flag = 0;
		updateCookie();
		return flag;
	}
	
	//向账号管理Server请求分配账号
	private boolean requestAllocateAccount(){
		boolean flag = false;
		if(!this.rpcClient.isConnected()){
			LOG.error("Cannot connect to LoginAccount Server");
			return flag;
		}
		
		LOG.info("Request Login Account for siteId: " + siteId);
		int isShare = 0;
		if(isShareAccount()){
			isShare = 1;
		}
		
		TaskRunnerReqAccountWritable reqWritable = new TaskRunnerReqAccountWritable(isShare, siteId, agentSubgroupId);
		synchronized (rpcClient){
			ResTaskRunnerReqAccountWritable accountInfo = (ResTaskRunnerReqAccountWritable) rpcClient.execute_proxy(ServiceName.TASK_RUNNER_REQUEST_ACCOUNT.getName(), reqWritable);
			if(accountInfo != null) {
				isAutoLogin = accountInfo.getIsAutoLogin()>0;
				userName = accountInfo.getUserName();
				passwd = accountInfo.getPasswd();
				accountId = accountInfo.getAccountId();
				cookie = accountInfo.getCookie();
				
				flag = true;
				
				LOG.info("Request Login Account success; " + accountInfo.toString());
			} else {
				LOG.error("NetError: Send request account to LoginAccountManager failed");
			}
		}
		
		return flag;
	}
	
	//更新cookie
	private boolean updateCookie(){
		boolean flag = false;
		if(isAutoLogin && shareAccount){
			//将cookie更新到登陆账号管理Server
			TaskRunnerUpdateAccountCookieWritable updateAccountCookie = new TaskRunnerUpdateAccountCookieWritable();
			updateAccountCookie.setAccountId(accountId);
			updateAccountCookie.setSiteId(siteId);
			updateAccountCookie.setAgentSubgroupId(agentSubgroupId);
			updateAccountCookie.setIsShare(shareAccount?1:0);
			updateAccountCookie.setIsAutoLogin(isAutoLogin?1:0);
			updateAccountCookie.setStatus(status);
			updateAccountCookie.setCookie(cookie);
			
			synchronized (rpcClient){
				FeedBackWritable feedBack = (FeedBackWritable) rpcClient.execute_proxy(ServiceName.TASK_RUNNER_UPDATE_ACCOUNT.getName(), updateAccountCookie);
				if(feedBack != null) {
					if(feedBack.getCode() == CodeStatus.succCode) {
						flag = true;
					}else{
						LOG.info("Update Account Cookie for " + accountId + " failed: " + feedBack.toString());
					}
				} else {
					LOG.error("NetError: Update Account Cookie to LoginAccountManager failed");
				}
			}
		}
		return flag;
	}
	
	//归还账号：通知server，cookie已经使用完毕
	public boolean returnAccount() {
		boolean flag = false;
		
		TaskRunnerReturnAccountWritable returnWritable = new TaskRunnerReturnAccountWritable();
		returnWritable.setAccountId(accountId);
		returnWritable.setSiteId(siteId);
		returnWritable.setAgentSubgroupId(agentSubgroupId);
		returnWritable.setIsShare(shareAccount?1:0);
		returnWritable.setIsAutoLogin(isAutoLogin?1:0);
		returnWritable.setStatus(status);
		
		synchronized (rpcClient){
			FeedBackWritable feedBack = (FeedBackWritable) rpcClient.execute_proxy(ServiceName.TASK_RUNNER_RETURN_ACCOUNT.getName(), returnWritable);
			if(feedBack != null) {
				if(feedBack.getCode() == CodeStatus.succCode) {
					flag = true;
				}
				LOG.info("Return Account to LoginAccountManager success; " + feedBack.toString());
			} else {
				LOG.error("NetError: Return Account to LoginAccountManager failed");
			}
		}
			
		return flag;
	}
	
	//更换账号
	public boolean changeAccount() {
		return false;
	}
}
