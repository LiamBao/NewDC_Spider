package com.cic.datacollection.rpc.impl;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacollection.anew.RpcAgentDaemon;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;
import com.cic.datacrawl.core.rpc.protocol.RecordCollectWritable;

public class AgentRecordCollectExeImpl implements ExecuteInterface {
	private static final Logger log = Logger.getLogger(AgentRecordCollectExeImpl.class);
	@Override
	public Writable execute(Writable args) {
		log.info("receive the protocol from taskRunner");
		
		RecordCollectWritable collectWritable = (RecordCollectWritable) args;
		RpcAgentDaemon agent = RpcAgentDaemon.getInstance();
		FeedBackWritable writable = (FeedBackWritable)agent.sendRecordsToRecordCollectServer(collectWritable,true);
		
		return writable;
	}
}
