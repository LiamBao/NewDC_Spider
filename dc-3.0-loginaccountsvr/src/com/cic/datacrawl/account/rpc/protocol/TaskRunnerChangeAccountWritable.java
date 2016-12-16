package com.cic.datacrawl.account.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * 更换账号协议
 * 
 * @author charles.chen
 * 
 */
public class TaskRunnerChangeAccountWritable implements Writable {
	public int isShare;// 新账号是否共享
	public int siteId;
	public int agentSubgroupId;
	public int accountId;//被更换账号ID
	public int status;//被更换账号状态
	public int isAutoLogin;//被更换账号是否自动登录：0：非自动登录 1：自动登录
	public int oldAccountIsShare;// 被更换账号的是否共享标记
	
	public TaskRunnerChangeAccountWritable() {
	}

	public int getIsAutoLogin() {
		return isAutoLogin;
	}

	public void setIsAutoLogin(int isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
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

	
	public int getOldAccountIsShare() {
		return oldAccountIsShare;
	}

	public void setOldAccountIsShare(int oldAccountIsShare) {
		this.oldAccountIsShare = oldAccountIsShare;
	}

	@Override
	public void readFields(DataInput out) throws IOException {
		isShare = out.readInt();
		siteId = out.readInt();
		agentSubgroupId = out.readInt();
		accountId = out.readInt();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(isShare);
		in.writeInt(siteId);
		in.writeInt(agentSubgroupId);
		in.writeInt(accountId);
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("ReqLoginAccount: [isShare: " + isShare + ",siteId: "
				+ siteId + ",agentSubgroupId: " + agentSubgroupId
				+ ",accountId: " + accountId + ",status: " + status 
				+ ",isAutoLogin: " + isAutoLogin + ",oldAccountIsShare: " + oldAccountIsShare +"]");
		return strBuf.toString();
	}
}
