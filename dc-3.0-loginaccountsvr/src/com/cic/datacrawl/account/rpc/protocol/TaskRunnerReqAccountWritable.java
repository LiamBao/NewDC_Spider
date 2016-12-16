package com.cic.datacrawl.account.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
/**
 * 请求账号协议
 * @author charles.chen
 *
 */
public class TaskRunnerReqAccountWritable implements Writable {
	public int isShare;
	public int siteId;
	public int agentSubgroupId;
	
	public TaskRunnerReqAccountWritable() {
	}
	
	public TaskRunnerReqAccountWritable(int isShare, int siteId, int agentSubgroupId) {
		this.isShare = isShare;
		this.siteId = siteId;
		this.agentSubgroupId = agentSubgroupId;
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


	@Override
	public void readFields(DataInput out) throws IOException {
		isShare = out.readInt();
		siteId = out.readInt();
		agentSubgroupId = out.readInt();
	}

	@Override
	public void write(DataOutput in) throws IOException {
		in.writeInt(isShare);
		in.writeInt(siteId);
		in.writeInt(agentSubgroupId);
	}

	@Override
	public String toString(){
		StringBuffer strBuf = new StringBuffer();  
		strBuf.append("ReqLoginAccount: [isShare: " + isShare + ",siteId: " + siteId + ",agentSubgroupId: " + agentSubgroupId + "]");  
		return strBuf.toString(); 
	}
}
