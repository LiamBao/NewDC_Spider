//DON'T MODIFY ME
package com.cic.datacrawl.management.dao.base.oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.cic.datacrawl.management.dao.base.AccountBaseDAO;
import com.cic.datacrawl.management.entity.Account;
import com.cic.datacrawl.management.utils.DBUtils;

public class AccountBaseDAOImpl extends JdbcDaoSupport implements
		AccountBaseDAO {

	public int count() {
		String sql = "select count(*) from t_site_login_account";
		return getJdbcTemplate().queryForInt(sql);
	}

	public Account[] getAllAccount() {
		return getAllAccount(0, -1);
	}

	public Account[] getAllAccount(int startIndex, int limit) {
		String sql = "select id, site_id, account, password, last_get_time, last_get_key, is_invalid from t_site_login_account order by id";
		String paginationSQL = DBUtils.buildPageSQL(startIndex, limit, sql);
		logger.debug("SQL: " + paginationSQL);
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(paginationSQL, processor);
		return (Account[]) processor.result.toArray(new Account[0]);
	}



	public Account getAccount(long id) {
		String sql = "select id, site_id, account, password, last_get_time, last_get_key, is_invalid from t_site_login_account where id="
				+ id;
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0) {
			return (Account) processor.result.get(0);
		} else {
			return null;
		}
	}

	public Account[] getAccounts(final long[] ids) {
		if (ids.length == 0) {
			return new Account[0];
		}
		StringBuffer sql = new StringBuffer(
				"select id, site_id, account, password, last_get_time, last_get_key, is_invalid from t_site_login_account where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by id");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), processor);
		return (Account[]) processor.result.toArray(new Account[0]);
	}

	public long addAccount(final Account account) {
		if (account == null) {
			throw new IllegalArgumentException("添加的Account为null");
		}
		String sqlSeq = "select S_T_SITE_LOGIN_ACCOUNT.nextval from dual";
		final long id = getJdbcTemplate().queryForLong(sqlSeq);

		String sql = "insert into T_SITE_LOGIN_ACCOUNT (id,site_id, account, password, last_get_time, last_get_key, is_invalid) "
				+ "values(?,?,?,?,?,?,?)";

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, id);
				ps.setLong(index++, account.getSiteId());
				ps.setString(index++, account.getAccount());
				ps.setString(index++, account.getPassword());
				ps.setTimestamp(index++, account.getLastGetTime());
				ps.setString(index++, account.getLastGetKey());
				ps.setByte(index++, account.getInvalid());
			}
		});

		return id;
	}

	public long[] addAccounts(final Account[] accounts) {
		if (accounts == null || accounts.length == 0) {
			throw new IllegalArgumentException("添加的Account为null");
		}

		long[] ret = new long[accounts.length];
		for (int i = 0; i < accounts.length; ++i) {
			ret[i] = addAccount(accounts[i]);
		}
		return ret;
	}

	public int deleteAccount(long id) {
		String sql = "delete from t_site_login_account where id=?";
		return getJdbcTemplate().update(sql.toString(),
				new Object[] { new Long(id) },
				new int[] { java.sql.Types.BIGINT });
	}

	public int deleteAccounts(long[] ids) {
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer(
				"delete from t_site_login_account where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		return getJdbcTemplate().update(sql.toString());
	}

	public int saveAccount(final Account account) {
		String sql = "update t_site_login_account set site_id=?, account=?, password=?, last_get_time=?, last_get_key=?, is_invalid=? where id = ?";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, account.getSiteId());
				ps.setString(index++, account.getAccount());
				ps.setString(index++, account.getPassword());
				ps.setTimestamp(index++, account.getLastGetTime());
				ps.setString(index++, account.getLastGetKey());
				ps.setByte(index++, account.getInvalid());
				ps.setLong(index++, account.getId());
			}
		});
	}

	public int[] saveAccounts(final Account[] accounts) {
		if (accounts == null || accounts.length == 0) {
			throw new IllegalArgumentException("更新的Account为null");
		}
		String sql = "update t_site_login_account set site_id=?, account=?, password=?, last_get_time=?, last_get_key=?, is_invalid=? where id = ?";

		return getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						int index = 1;
						ps.setLong(index++, accounts[i].getSiteId());
						ps.setString(index++, accounts[i].getAccount());
						ps.setString(index++, accounts[i].getPassword());
						ps.setTimestamp(index++, accounts[i].getLastGetTime());
						ps.setString(index++, accounts[i].getLastGetKey());
						ps.setByte(index++, accounts[i].getInvalid());
						ps.setLong(index++, accounts[i].getId());
					}

					public int getBatchSize() {
						return accounts.length;
					}
				});
	}

	public Account[] queryAccountBySiteId(final long siteId, final byte invalid) {

		StringBuffer sql = new StringBuffer(
				"select id, site_id, account, password, last_get_time, last_get_key, is_invalid from t_site_login_account where site_id=? AND is_invalid=? ");
		String orderBy = "";
		orderBy = "last_get_time asc";
		if (orderBy != null && orderBy.length() > 0) {
			sql.append(" order by ").append(orderBy);
		}

		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, siteId);
				ps.setByte(2, invalid);
			}
		}, processor);
		return (Account[]) processor.result.toArray(new Account[0]);
	}

	public Account queryAccount(final long id, final java.lang.String lastGetKey) {

		StringBuffer sql = new StringBuffer(
				"select id, site_id, account, password, last_get_time, last_get_key, is_invalid from t_site_login_account where id=? AND last_get_key=? ");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, id);
				ps.setString(2, lastGetKey);
			}
		}, processor);

		if (processor.result.size() > 0) {
			return (Account) processor.result.get(0);
		} else {
			return null;
		}
	}

	protected class CountProcess implements RowCallbackHandler {
		public CountProcess() {
		}

		private long count = 0;

		public long getCount() {
			return count;
		}

		@Override
		public void processRow(ResultSet rs) throws SQLException {
			count = rs.getLong("count");
		}
	}

	protected class IDProcessor implements RowCallbackHandler {
		public IDProcessor() {
		}

		public List<Long> result = new ArrayList<Long>();

		public void processRow(ResultSet rs) throws SQLException {
			result.add(rs.getLong("id"));
		}
	};

	protected class EntityProcessor implements RowCallbackHandler {
		public EntityProcessor() {
		}

		public List<Account> result = new ArrayList<Account>();

		public void processRow(ResultSet rs) throws SQLException {
			Account account = new Account();
			account.setId(rs.getLong("id"));
			account.setSiteId(rs.getLong("site_id"));
			account.setAccount(rs.getString("account"));
			account.setPassword(rs.getString("password"));
			java.sql.Timestamp lastGetTime = rs.getTimestamp("last_get_time");
			if (lastGetTime != null) {
				account.setLastGetTime(lastGetTime);
			}
			account.setLastGetKey(rs.getString("last_get_key"));
			account.setInvalid(rs.getByte("is_invalid"));
			result.add(account);
		}
	};
}
