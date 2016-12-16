package com.cic.datacrawl.core.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

public class SQLUtil {
	public static int parseSQLType(Object o) {
		if (o == null) {
			return Types.NULL;
		}
		if (o instanceof Timestamp) {
			return Types.TIMESTAMP;
		}
		if (o instanceof Long) {
			return Types.BIGINT;
		}
		if (o instanceof Boolean) {
			return Types.BOOLEAN;
		}
		if (o instanceof String) {
			return Types.VARCHAR;
		}
		if (o instanceof Double) {
			return Types.DOUBLE;
		}
		if (o instanceof Date) {
			return Types.TIMESTAMP;
		}
		if (o instanceof Float) {
			return Types.FLOAT;
		}
		if (o instanceof Short) {
			return Types.SMALLINT;
		}
		if (o instanceof Integer) {
			return Types.INTEGER;
		}
		return Types.JAVA_OBJECT;
	}

	public static void setValue(PreparedStatement ps, int type, Object value, int index) throws SQLException {
		if (value == null) {
			ps.setNull(index, type);
		} else {
			switch (type) {
			case Types.TIMESTAMP:
				Timestamp date = null;
				if (value instanceof Timestamp) {
					date = (Timestamp) value;
				} else if (value instanceof Date) {
					date = new Timestamp(((Date) value).getTime());
				} else if (value instanceof Number) {
					date = new Timestamp(((Number) value).longValue());
				}

				ps.setTimestamp(index, date);
				break;
			case Types.BIGINT:
				ps.setLong(index, new Long(((Number) value).longValue()));
				break;
			case Types.BOOLEAN:
				ps.setBoolean(index, (Boolean) value);
				break;
			case Types.LONGVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.NVARCHAR:
			case Types.VARCHAR:
				ps.setString(index, (String) value);
				break;
			case Types.NUMERIC:
			case Types.DOUBLE:
				ps.setDouble(index, ((Number) value).doubleValue());
				break;
			case Types.DATE:
				ps.setDate(index, new java.sql.Date(((Date) value).getTime()));
				break;
			case Types.FLOAT:
				ps.setFloat(index, ((Number) value).floatValue());
				break;
			case Types.SMALLINT:
				ps.setShort(index, ((Number) value).shortValue());
				break;
			case Types.INTEGER:
				ps.setInt(index, ((Number) value).intValue());
				break;

			default:
				ps.setObject(index, value);
				break;
			}
		}
	}

}
