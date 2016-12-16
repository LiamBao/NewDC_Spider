package com.cic.datacrawl.management.manager;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.management.entity.Account;
import com.cic.datacrawl.management.manager.base.AccountBaseManager;

public class AccountManager extends AccountBaseManager {
	
	private final static Logger log = Logger.getLogger(AccountManager.class);
	
	public Account getNextAccount(long siteId) {
		Account[] accounts = getAccountDAO().queryAccountBySiteId(siteId, (byte) 0);

		String key = StringUtil.buildRandomString(10);
		for (int i = 0; i < accounts.length; ++i) {
			try{
				getAccountDAO().updateKey(accounts[i].getId(), key, accounts[i].getLastGetKey());
			}catch(Exception e){
				log.error(e.getMessage(),e);
			}
			Account ret = queryAccount(accounts[i].getId(), key);
			if (ret != null)
				return ret;
		}

		return null;
	}

	public void invalidAccount(long siteId, String username) {
		getAccountDAO().invalidAccount(siteId, username);
	}

}
