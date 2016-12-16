package com.cic.datacrawl.core.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.SQLUtil;

public class BatchPreparedStatementSetterImpl implements
		BatchPreparedStatementSetter {
	private BaseEntity[] entities;
	private Map<String, Object> whereMap;

	public BatchPreparedStatementSetterImpl(BaseEntity[] entities) {
		this.entities = entities;
	}
	
		
	public BatchPreparedStatementSetterImpl(BaseEntity[] entities,
			Map<String, Object> whereMap) {
		this.entities = entities;
		this.whereMap = whereMap;
	}

	@Override
	public int getBatchSize() {
		if (entities == null)
			return 0;
		return entities.length;
	}

	@Override
	public void setValues(PreparedStatement ps, int index) throws SQLException {
		String[] keys = entities[index].getEntityKeys();
		int l = 1;
		for (int i = 0; i < keys.length; ++i) {
			Object value = entities[index].get(keys[i]);
			int type = SQLUtil.parseSQLType(value);
			ps.setObject(l, value, type);
			l++;
		}
		if (whereMap != null && whereMap.size() > 0) {
			Set<String> keySet = whereMap.keySet();
			keys = new String[keySet.size()];
			keySet.toArray(keys);
			for (int i = 0; i < keys.length; ++i) {
				Object value = whereMap.get(keys[i]);
				int type = SQLUtil.parseSQLType(value);
				ps.setObject(l, value, type);
				l++;
			}
		}
	}

}
