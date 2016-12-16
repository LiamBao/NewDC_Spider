//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base;
import com.cic.datacrawl.management.entity.Account;

public interface AccountBaseDAO {

	public Account[] getAllAccount();

	public Account[] getAllAccount(int startIndex, int limit);

	public Account getAccount(long id);

	public Account[] getAccounts(long[] id);
	
	public long addAccount(Account account);
	
	public long[] addAccounts(Account[] accounts);
	
	public int deleteAccount(long id);
	
	public int deleteAccounts(long[] id);
	
	public int saveAccount(Account account);
	
	public int[] saveAccounts(final Account[] accounts);
	
	public int count();
	
	public Account[] queryAccountBySiteId(final long siteId, final byte invalid );
	public Account queryAccount(final long id, final java.lang.String lastGetKey);
}
