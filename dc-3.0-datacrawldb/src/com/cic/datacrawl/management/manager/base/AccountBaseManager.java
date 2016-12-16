//DON'T MODIFY ME
package com.cic.datacrawl.management.manager.base;

import com.cic.datacrawl.management.entity.Account;
import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.management.dao.AccountDAO;

public abstract class AccountBaseManager{

	protected AccountDAO dao;
	protected AccountDAO getAccountDAO() {		
		if(dao == null){
			dao = (AccountDAO) ApplicationContext.getInstance().getBean("accountDAO");
		}
		return dao;
	}
	
	public void setAccountDAO(AccountDAO dao) {
		this.dao = dao;
	}
	
	public Account[] getAllAccount() {
		return getAccountDAO().getAllAccount();
	}
	
	public Account[] getAllAccount(int startIndex, int limit) {
		return getAccountDAO().getAllAccount(startIndex, limit);
	}
	
	public Account getAccount(long id){
		return getAccountDAO().getAccount(id);
	}

	public Account[] getAccounts(long[] ids){
		return getAccountDAO().getAccounts(ids);
	}
	
	public long addAccount(Account account){
		return getAccountDAO().addAccount(account);
	}	
	
	public long[] addAccounts(Account[] accounts){
		return getAccountDAO().addAccounts(accounts);
	}
	
	public int deleteAccount(long id){
		return getAccountDAO().deleteAccount(id);
	}
	
	public int deleteAccounts(long[] ids){
		return getAccountDAO().deleteAccounts(ids);
	}
	
	public long saveAccount(Account account){
		long ret = getAccountDAO().saveAccount(account);
		if (ret == 0) {
			ret = getAccountDAO().addAccount(account);
		} else {
			ret = account.getId();
		}
		return ret;
	}
	
	public int updateAccount(Account account){
		return getAccountDAO().saveAccount(account);		
	}
	
	public int[] updateAccounts(Account[] accounts){
		return getAccountDAO().saveAccounts(accounts);		
	}
	
	public int count(){
		return getAccountDAO().count();
	}
	
	public Account[] queryAccountBySiteId(final long siteId, final byte invalid ){
		 return getAccountDAO().queryAccountBySiteId(siteId,invalid );
	}
	public Account queryAccount(final long id, final java.lang.String lastGetKey ){
		return getAccountDAO().queryAccount(id,lastGetKey);
	}
}
