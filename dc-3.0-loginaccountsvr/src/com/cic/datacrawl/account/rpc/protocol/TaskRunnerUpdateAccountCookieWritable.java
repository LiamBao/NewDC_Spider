package com.cic.datacrawl.account.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
/**
 * 上传账号cookie协议
 * @author charles.chen
 *
 */
public class TaskRunnerUpdateAccountCookieWritable implements Writable {
	public int siteId;
	public int agentSubgroupId;
	public int accountId;
	public int status;//1-账号有效 2-cookie失效 3-账号登录失败
	public int isShare;
	public int isAutoLogin;//是否自动登录：0：非自动登录 1：自动登录
	public String cookie;
	
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

	public int getIsShare() {
		return isShare;
	}

	public void setIsShare(int isShare) {
		this.isShare = isShare;
	}

	public int getIsAutoLogin() {
		return isAutoLogin;
	}

	public void setIsAutoLogin(int isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
	}

	
	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		if(cookie == null){
			cookie = "";
		}
		this.cookie = cookie;
	}

	@Override
	public void readFields(DataInput out) throws IOException {
		siteId = out.readInt();
		agentSubgroupId = out.readInt();
		accountId = out.readInt();
		status = out.readInt();
		isShare = out.readInt();
		isAutoLogin = out.readInt();
		cookie = out.readUTF();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(siteId);
		in.writeInt(agentSubgroupId);
		in.writeInt(accountId);
		in.writeInt(status);
		in.writeInt(isShare);
		in.writeInt(isAutoLogin);
		in.writeUTF(cookie);
	}
	
	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("ReqLoginAccount: [ siteId: " + siteId + ",agentSubgroupId: " + agentSubgroupId + 
				",accountId: " + accountId + ",status: " + status + ",isShare: " + isShare + 
				",isAutoLogin: " + isAutoLogin + ",cookie: " + cookie + "]");  
        return strBuf.toString(); 
	}
}
