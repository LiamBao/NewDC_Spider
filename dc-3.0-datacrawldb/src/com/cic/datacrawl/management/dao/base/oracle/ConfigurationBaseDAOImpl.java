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

import com.cic.datacrawl.management.dao.base.ConfigurationBaseDAO;
import com.cic.datacrawl.management.entity.Configuration;
import com.cic.datacrawl.management.utils.DBUtils;

public class ConfigurationBaseDAOImpl extends JdbcDaoSupport implements
		ConfigurationBaseDAO {

	public int count() {
		String sql = "select count(*) from t_configuration";
		return getJdbcTemplate().queryForInt(sql);
	}

	public Configuration[] getAllConfiguration() {
		return getAllConfiguration(0, -1);
	}

	public Configuration[] getAllConfiguration(int startIndex, int limit) {
		String sql = "select id, name, value from t_configuration order by id";
		String paginationSQL = DBUtils.buildPageSQL(startIndex, limit, sql);
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(paginationSQL, processor);
		return (Configuration[]) processor.result.toArray(new Configuration[0]);
	}

	public Configuration getConfiguration(long id) {
		String sql = "select id, name, value from t_configuration where id="
				+ id;
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql, processor);
		if (processor.result.size() > 0) {
			return (Configuration) processor.result.get(0);
		} else {
			return null;
		}
	}

	public Configuration[] getConfigurations(final long[] ids) {
		if (ids.length == 0) {
			return new Configuration[0];
		}
		StringBuffer sql = new StringBuffer(
				"select id, name, value from t_configuration where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(") order by id");
		EntityProcessor processor = new EntityProcessor();
		getJdbcTemplate().query(sql.toString(), processor);
		return (Configuration[]) processor.result.toArray(new Configuration[0]);
	}

	public long addConfiguration(final Configuration configuration) {
		if (configuration == null) {
			throw new IllegalArgumentException("添加的Configuration为null");
		}

		String sqlSeq = "select S_T_CONFIGURATION.nextval from dual";
		final long id = getJdbcTemplate().queryForLong(sqlSeq);

		String sql = "insert into t_configuration (id,name, value) "
				+ "values(?,?,?)";

		JdbcTemplate theJdbcTemplate = getJdbcTemplate();
		theJdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, id);
				ps.setString(index++, configuration.getName());
				ps.setString(index++, configuration.getValue());
			}
		});

		return id;
	}

	public long[] addConfigurations(final Configuration[] configurations) {
		if (configurations == null || configurations.length == 0) {
			throw new IllegalArgumentException("添加的Configuration为null");
		}

		long[] ret = new long[configurations.length];
		for (int i = 0; i < configurations.length; ++i) {
			ret[i] = addConfiguration(configurations[i]);
		}
		return ret;
	}

	public int deleteConfiguration(long id) {
		String sql = "delete from t_configuration where id=?";
		return getJdbcTemplate().update(sql.toString(),
				new Object[] { new Long(id) },
				new int[] { java.sql.Types.BIGINT });
	}

	public int deleteConfigurations(long[] ids) {
		if (ids.length == 0) {
			return 0;
		}
		StringBuffer sql = new StringBuffer(
				"delete from t_configuration where id in (");
		for (int i = 0; i < ids.length - 1; i++) {
			sql.append(ids[i]).append(",");
		}
		sql.append(ids[ids.length - 1]).append(")");
		return getJdbcTemplate().update(sql.toString());
	}

	public int saveConfiguration(final Configuration configuration) {
		String sql = "update t_configuration set name=?, value=? where id = ?";
		return getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, configuration.getName());
				ps.setString(index++, configuration.getValue());
				ps.setLong(index++, configuration.getId());
			}
		});
	}

	public int[] saveConfigurations(final Configuration[] configurations) {
		if (configurations == null || configurations.length == 0) {
			throw new IllegalArgumentException("更新的Configuration为null");
		}
		String sql = "update t_configuration set name=?, value=? where id = ?";

		return getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						int index = 1;
						ps.setString(index++, configurations[i].getName());
						ps.setString(index++, configurations[i].getValue());
						ps.setLong(index++, configurations[i].getId());
					}

					public int getBatchSize() {
						return configurations.length;
					}
				});
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

		public List<Configuration> result = new ArrayList<Configuration>();

		public void processRow(ResultSet rs) throws SQLException {
			Configuration configuration = new Configuration();
			configuration.setId(rs.getLong("id"));
			configuration.setName(rs.getString("name"));
			configuration.setValue(rs.getString("value"));
			result.add(configuration);
		}
	};
}
