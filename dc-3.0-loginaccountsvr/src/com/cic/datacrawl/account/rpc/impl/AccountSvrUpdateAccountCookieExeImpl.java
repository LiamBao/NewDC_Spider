package com.cic.datacrawl.account.rpc.impl;

import org.apache.hadoop.io.Writable;

import com.cic.datacrawl.account.anew.AccountServerDaemon;
import com.cic.datacrawl.account.rpc.protocol.TaskRunnerUpdateAccountCookieWritable;
import com.cic.datacrawl.core.rpc.ExecuteInterface;
import com.cic.datacrawl.core.rpc.protocol.FeedBackWritable;
/**
 * 自动登录账号登陆后，上传cookie
 * Server将账号的Cookie添加对站点对应线路账号列表中，并标识站点列表中该账号为被使用状态，更新cache
 * @author charles.chen
 *
 */
public class AccountSvrUpdateAccountCookieExeImpl implements ExecuteInterface {

	@Override
	public Writable execute(Writable args) {
		TaskRunnerUpdateAccountCookieWritable writable = (TaskRunnerUpdateAccountCookieWritable)args;
		
		FeedBackWritable backWritable = AccountServerDaemon.getInstance().updateAccountCookieByWritable(writable);
		return backWritable;
	}

}
