package com.cic.datacrawl.account.rpc.impl;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import com.cic.datacrawl.account.anew.AccountServerDaemon;
import com.cic.datacrawl.account.rpc.protocol.ResTaskRunnerReqAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerReqAccountWritable;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
/**
 * 分配账号
 * @author charles.chen
 *
 */
public class AccountSvrAllocateAccountExeImpl implements ExecuteInterface {
	private static final Logger log = Logger.getLogger(AccountSvrAllocateAccountExeImpl.class);
	@Override
	public Writable execute(Writable args) {
		log.info(" request an account by TaskRunnerReqAccountWritable ");
		TaskRunnerReqAccountWritable writable = (TaskRunnerReqAccountWritable)args;
		ResTaskRunnerReqAccountWritable resWritable = new ResTaskRunnerReqAccountWritable();
		try {
			resWritable = AccountServerDaemon.getInstance().getAccountByWritable(writable);
			log.info("request an account sucess!");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("request an account failed!", e);
		}
		
		return resWritable;
	}

}
