package com.cic.datacrawl.core.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.PreparedStatementSetter;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.util.SQLUtil;

public class PreparedStatementSetterImpl implements PreparedStatementSetter {

	private BaseEntity entity;
	private Map<String, Object> whereMap;

	public PreparedStatementSetterImpl(BaseEntity entity) {
		this.entity = entity;
	}

	public PreparedStatementSetterImpl(BaseEntity entity,
			Map<String, Object> whereMap) {
		this.entity = entity;
		this.whereMap = whereMap;
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		String[] keys = entity.getEntityKeys();
		int index = 1;
		for (int i = 0; i < keys.length; ++i) {
			Object value = entity.get(keys[i]);
			int type = SQLUtil.parseSQLType(value);
			ps.setObject(index, value, type);
			index++;
		}
		if (whereMap != null && whereMap.size() > 0) {
			Set<String> keySet = whereMap.keySet();
			keys = new String[keySet.size()];
			keySet.toArray(keys);
			for (int i = 0; i < keys.length; ++i) {
				Object value = whereMap.get(keys[i]);
				int type = SQLUtil.parseSQLType(value);
				ps.setObject(index, value, type);
				index++;
			}
		}
	}

}
