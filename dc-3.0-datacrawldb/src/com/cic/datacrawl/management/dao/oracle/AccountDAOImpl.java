package com.cic.datacrawl.management.dao.oracle;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;

import com.cic.datacrawl.management.dao.AccountDAO;
import com.cic.datacrawl.management.dao.base.oracle.AccountBaseDAOImpl;

public class AccountDAOImpl extends AccountBaseDAOImpl implements AccountDAO {

	@Override
	public int updateKey(final long id, final String newKey, final String key) {

		String sql = "update t_site_login_account set last_get_time=sysdate,last_get_key=? where id = ? and (last_get_key=? or last_get_key is null)";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, newKey);
				ps.setLong(index++, id);
				ps.setString(index++, key);
			}
		});
	}

	@Override
	public int invalidAccount(final long siteId, final String username) {
		String sql = "update t_site_login_account set invalid=1 where account=? AND site_id in (select id from t_site where id = ? OR ref_site_id = ?)";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, username);
				ps.setLong(index++, siteId);
				ps.setLong(index++, siteId);
			}
		});
	}

}
