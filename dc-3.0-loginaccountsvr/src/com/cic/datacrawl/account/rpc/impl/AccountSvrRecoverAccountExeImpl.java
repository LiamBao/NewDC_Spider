package com.cic.datacrawl.account.rpc.impl;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacrawl.account.anew.AccountServerDaemon;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerReturnAccountWritable;
import com.cic.datacrawl.core.rpc.CodeStatus;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;
/**
 * 归还账号
 * @author charles.chen
 *
 */
public class AccountSvrRecoverAccountExeImpl implements ExecuteInterface {
	private static final Logger log = Logger.getLogger(AccountSvrRecoverAccountExeImpl.class);
	@Override
	public Writable execute(Writable args) {
		log.info(" recover an account by TaskRunnerReturnAccountWritable ");
		TaskRunnerReturnAccountWritable writable = (TaskRunnerReturnAccountWritable)args;
		FeedBackWritable backWritable = new FeedBackWritable(CodeStatus.failCode, "", "");
		try {
			backWritable = AccountServerDaemon.getInstance().recoverAccountByWritable(writable);
			log.info("recover an account success!");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("recover an account failed", e);
		}
		
		return backWritable;
	}

}
