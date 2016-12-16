package com.cic.datacrawl.core.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.DefaultEntity;

public class DefaultDAO extends BaseDAO {

	@Override
	protected BaseEntity createEntity(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();

		DefaultEntity entity = new DefaultEntity(meta.getTableName(1));
		int columnNumber = meta.getColumnCount();
		for (int i = 0; i < columnNumber;) {
			String columnName = meta.getColumnName(++i);
			int type = meta.getColumnType(i);
			entity.set(columnName, getObject(rs, columnName, type));
		}
		return entity;
	}

	private Object getObject(ResultSet rs, String columnName, int type) throws SQLException {
		switch (type) {
		case Types.TINYINT:	
		case Types.BIT:		
			return rs.getByte(columnName);
		case Types.SMALLINT:
			return rs.getShort(columnName);
		case Types.BIGINT:
			return rs.getLong(columnName);
		case Types.INTEGER:
			return rs.getInt(columnName);
		case Types.BOOLEAN:
			return rs.getBoolean(columnName);
		case Types.FLOAT:
			return rs.getFloat(columnName);
		case Types.DECIMAL:
		case Types.DOUBLE:
			return rs.getDouble(columnName);		
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
		case Types.CHAR:
			return rs.getString(columnName);
		case Types.TIME:
			return rs.getTime(columnName);
		case Types.DATE:
			return rs.getDate(columnName);
		case Types.TIMESTAMP:
			return rs.getTimestamp(columnName);
		case Types.NULL:
			return null;

		default:
			return null;
		}
	}

}
