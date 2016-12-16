/*****************************************************************<br>
 * <B>FILE :</B> WsCoreServiceClientTest <br>
 * <B>CREATE DATE :</B> Jul 20, 2010 <br>
 * <B>DESCRIPTION :</B> <br>
 *
 * <B>CHANGE HISTORY LOG</B><br>
 *---------------------------------------------------------------<br>
 * NO.  |  DATE |   NAME   |   REASON   |  DESCRIPTION           <br>
 *---------------------------------------------------------------<br>
 *          
 *****************************************************************<br>
 */
package com.cic.datacrawl.management.test;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.cic.datacrawl.management.dao.base.AccountBaseDAO;
import com.cic.datacrawl.management.entity.Account;

/**
 * <B>Function :</B> <br>
 * <B>General Usage :</B> <br>
 * <B>Special Usage :</B> <br>
 * 
 * @author : jean.jiang<br>
 * @since : 2011-9-7<br>
 * @version : v1.0
 */
public class ManagementDaoTest extends
		AbstractDependencyInjectionSpringContextTests {
	private AccountBaseDAO accountBaseDAO;

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "beans/core/applicationContext-dc-init.xml",
				"beans/core/beans.database.xml",
				"beans/dcmanagement/beans_dcmanagement_dao.xml" };
	}

	protected void onSetUp() throws Exception {
		accountBaseDAO = (AccountBaseDAO) applicationContext
				.getBean("accountDAO");

	}

	public void testAccountDao() {
		Account account = new Account();
		account.setSiteId(222222);
		account.setAccount("ddddddddddddd");

		long id = accountBaseDAO.addAccount(account);

	}

	public void testgetAllAccount() {
		Account[] allAccount = accountBaseDAO.getAllAccount(5, 12);
		assertEquals(12, allAccount.length);

		allAccount = accountBaseDAO.getAllAccount(0, 12);
		assertEquals(12, allAccount.length);

	}

}
