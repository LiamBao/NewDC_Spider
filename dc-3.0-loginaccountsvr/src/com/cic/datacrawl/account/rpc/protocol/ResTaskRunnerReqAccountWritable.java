package com.cic.datacrawl.account.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
/**
 * 
 * 回复请求账号协议
 * 
 * @author charles.chen
 *
 */
public class ResTaskRunnerReqAccountWritable implements Writable {
	public int isAutoLogin;
	public int accountId;
	public String userName;
	public String passwd;
	public String cookie;
	
	public ResTaskRunnerReqAccountWritable() {
		accountId = 0;
		this.userName = "";
		this.passwd = "";
		this.cookie = "";
	}

	public int getIsAutoLogin() {
		return isAutoLogin;
	}

	public void setIsAutoLogin(int isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		if(userName == null){
			userName = "";
		}
		this.userName = userName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		if(passwd == null){
			passwd = "";
		}
		this.passwd = passwd;
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
		isAutoLogin = out.readInt();
		accountId = out.readInt();
		userName = out.readUTF();
		passwd = out.readUTF();
		cookie = out.readUTF();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(isAutoLogin);
		in.writeInt(accountId);
		in.writeUTF(userName);
		in.writeUTF(passwd);
		in.writeUTF(cookie);
	}

	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("ResLoginAccount: [isAutoLogin: " + isAutoLogin + ",accountId: " + accountId + ",userName: " + userName + ",passwd: " + passwd + ",cookie: " + cookie + "]");  
        return strBuf.toString(); 
	}
}
