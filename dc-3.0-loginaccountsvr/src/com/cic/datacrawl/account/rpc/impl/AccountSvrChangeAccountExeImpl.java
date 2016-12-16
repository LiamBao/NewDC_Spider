package com.cic.datacrawl.account.rpc.impl;

import org.apache.hadoop.io.Writable;

import com.cic.datacrawl.account.anew.AccountServerDaemon;
import com.cic.datacrawl.account.rpc.protocol.ResTaskRunnerReqAccountWritable;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerChangeAccountWritable;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
/**
 * 更换账号
 * @author charles.chen
 *
 */
public class AccountSvrChangeAccountExeImpl implements ExecuteInterface {

	@Override
	public Writable execute(Writable args) {
		
		TaskRunnerChangeAccountWritable writable = (TaskRunnerChangeAccountWritable)args;
		
		ResTaskRunnerReqAccountWritable resWritable = AccountServerDaemon.getInstance().changeAccountByTaskRunnerChangeAccountWritable(writable);
		
		return resWritable;
	}

}
