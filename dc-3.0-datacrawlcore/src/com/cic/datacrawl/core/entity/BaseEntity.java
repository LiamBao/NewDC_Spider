package com.cic.datacrawl.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.mozilla.javascript.NativeObjectUtil;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.util.ArrayUtil;
import com.cic.datacrawl.core.util.CompareUtil;
import com.cic.datacrawl.core.util.DateUtil;
import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.core.util.XMLUtil;

public abstract class BaseEntity implements Serializable, Comparable<BaseEntity>, Cloneable {
	private static final long serialVersionUID = 6107469798690725898L;
	protected static final BigDecimal BigDecimal_ZERO = new BigDecimal("0");
	protected static final BigInteger BigInteger_ZERO = new BigInteger("0");
	protected static final Byte Byte_ZERO = new Byte((byte) 0);
	protected static final Character Character_SPACE = new Character(' ');
	protected static final Double Double_ZERO = new Double(0.0D);
	protected static final Float Float_ZERO = new Float(0.0F);
	protected static final Integer Integer_ZERO = new Integer(0);
	protected static final Long Long_ZERO = new Long(0L);
	public final static String DATE_FORMAT_TAG = "_DATEFORMAT";
	protected static final Short Short_ZERO = new Short((short) 0);
	protected static final String String_Empty = "";
	protected boolean includeDel = false;
	protected boolean ignoreCase = false;
	protected String[] columns;
	private String[] compareColumn;
	protected Logger log;
	protected Map<String, Object> values;

	public BaseEntity() {
		values = newMap();
		log = Logger.getLogger(getClass());
		columns = initColumns();
		compareColumn = initCompareColumns();
	}

	public void clear(String name) {
		if (name == null || !values.containsKey(name)) {
			return;
		}
		values.remove(name);
	}

	public void clearAll() {
		values.clear();
	}

	public Map<String, Object> getConditionMap() {
		if (compareColumn == null || compareColumn.length == 0)
			return null;

		if (compareColumn.length == columns.length)
			return values;

		Map<String, Object> ret = new HashMap<String, Object>();
		for (int i = 0; i < compareColumn.length; ++i) {
			ret.put(compareColumn[i], get(compareColumn[i]));
		}

		return ret;
	}

	public void setNotParseName(String name, Object value) {
		if (name == null) {
			return;
		}
		name = name.toLowerCase();
		if (value == null) {
			values.remove(name);
		} else {
			if (value instanceof String)
				values.put(name, ((String) value).trim());
			else
				values.put(name, value);
		}
	}

	@Override
	public Object clone() {
		BaseEntity newBean = null;
		try {
			newBean = (BaseEntity) this.getClass().newInstance();
			Iterator<String> keyIterator = values.keySet().iterator();
			while (keyIterator.hasNext()) {
				String key = (String) keyIterator.next();
				newBean.set(key.toString(), clone(values.get(key)));
			}
		} catch (InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
		return newBean;
	}

	private Object clone(Object src) {
		if (src instanceof Integer) {
			return new Integer(((Integer) src).intValue());
		}
		if (src instanceof Timestamp) {
			return new Timestamp(((Timestamp) src).getTime());
		}
		if (src instanceof Date) {
			return new Date(((Date) src).getTime());
		}
		if (src instanceof Long) {
			return new Long(((Long) src).longValue());
		}
		if (src instanceof Double) {
			return new Double(((Double) src).doubleValue());
		}
		if (src instanceof Float) {
			return new Float(((Float) src).floatValue());
		}
		if (src instanceof Byte) {
			return new Byte(((Byte) src).byteValue());
		}
		if (src instanceof Short) {
			return new Short(((Short) src).shortValue());
		}
		if (src instanceof Character) {
			return new Character(((Character) src).charValue());
		}
		if (src instanceof String) {
			return ((String) src).toString();
		}
		return src;
	}

	public int compareTo(Map<String, Object> conditionMap) {
		if (conditionMap == null) {
			return 0;
		}
		String[] keys = new String[conditionMap.size()];
		conditionMap.keySet().toArray(keys);

		for (int i = 0; i < compareColumn.length; ++i) {
			for (int j = 0; j < keys.length; ++j) {
				if (keys[j] == compareColumn[i]) {
					int compareValue = CompareUtil.compareObject(get(compareColumn[i]), conditionMap.get(compareColumn[i]));
					if (compareValue < 0) {
						return -1;
					}
					if (compareValue > 0) {
						return 1;
					}
					keys[j] = null;
				}
			}
		}
		for (int j = 0; j < keys.length; ++j) {
			if (keys[j] != null) {
				int compareValue = CompareUtil.compareObject(get(keys[j]), conditionMap.get(keys[j]));
				if (compareValue < 0) {
					return -1;
				}
				if (compareValue > 0) {
					return 1;
				}
			}
		}

		return 0;
	}

	public int compareTo(BaseEntity o) {
		if (compareColumn == null)
			return 0;
		if (o == null)
			return 1;
		return compareTo(((BaseEntity) o).getValueMap());
	}

	public boolean contains(String name) {
		if (name == null) {
			return false;
		}
		return values.containsKey(name.toLowerCase());
	}

	public boolean contains(String name, String key) {
		if (name == null || key == null) {
			return false;
		}
		Object value = values.get(name.toLowerCase());
		if (value == null) {
			return false;
		}
		if (value instanceof Map) {
			return ((Map) value).containsKey(key);
		} else {
			return false;
		}
	}

	public boolean equals(Map<String, Object> conditionMap) {
		Iterator<String> keyIterator = conditionMap.keySet().iterator();
		while (keyIterator.hasNext()) {
			Object key = keyIterator.next();
			if (!values.containsKey(key) || !values.get(key).equals(conditionMap.get(key))) {
				return false;
			}
		}
		return true;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof BaseEntity))
			return false;
		if (!((BaseEntity) o).toDataString().equals(toDataString()))
			return false;
		return true;
	}

	public Object get(String name) {
		if (name == null) {
			return null;
		}
		name = name.toLowerCase();
		if (!values.containsKey(name)) {
			return null;
		}
		return NativeObjectUtil.jsObject2java(values.get(name));
	}

	public boolean getBoolean(String name) {
		Object o = get(name);
		if (o == null)
			return false;
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue();
		} else if (o instanceof String) {
			try {
				return (new Boolean((String) o)).booleanValue();
			} catch (Throwable e) {
				throw new RuntimeException(name + " is not a Boolean, it is '" + o + "'", e);
			}
		} else {
			throw new RuntimeException(name + " is not a Boolean, it is '" + o + "'");
		}
	}

	public byte getByte(String name) {
		Object o = get(name);
		if (o == null)
			return 0;
		if (o instanceof Byte) {
			return ((Byte) o).byteValue();
		} else if (o instanceof Number) {
			return ((Number) o).byteValue();
		} else if (o instanceof Character) {
			return (byte) ((Character) o).charValue();
		} else if (o instanceof String) {
			byte[] bytes = ((String) o).getBytes();
			if (bytes.length > 0)
				return bytes[0];
		}
		return 0;
	}

	private Number getNumber(String name) {
		Object o = get(name);
		if (o == null)
			return null;
		if (o instanceof Number) {
			return (Number) o;
		} else if (o instanceof String) {
			try {
				return (new Double((String) o));
			} catch (Throwable e) {
				return 0;
			}
		} else {
			return 0;
		}
	}

	private Number[] getNumberArray(String name) {
		Object o = get(name);
		if (o == null)
			return null;
		if (o instanceof Number) {
			try {
				return new Number[] { (Number) o };
			} catch (Throwable e) {
				throw new RuntimeException(name + " is not a number, it is '" + o + "'", e);
			}
		} else if (o instanceof String) {
			try {
				Number[] ret = new Number[1];
				ret[0] = new Double((String) o);
				return ret;
			} catch (Throwable e) {
				throw new RuntimeException(name + " is not a number, it is '" + o + "'", e);
			}
		} else if (o instanceof Number[]) {
			return (Number[]) o;
		} else if (o instanceof String) {
			String str = (String) o;
			Number[] ret = new Number[1];
			try {
				ret[0] = new Double(str);
			} catch (Exception e) {
				ret = new Number[0];
			}
			return ret;
		} else if (o instanceof String[]) {
			String[] strArray = (String[]) o;
			Double[] ret = new Double[strArray.length];
			try {
				for (int i = 0; i < strArray.length; ++i) {
					ret[i] = new Double((String) strArray[i]);
				}
			} catch (Throwable e) {
				throw new RuntimeException(name + " is not a number, it is '" + o + "'", e);
			}
			return ret;
		} else {
			throw new RuntimeException(name + " is not a number, it is '" + o + "'");
		}
	}

	public char getCharacter(String name) {
		Object o = get(name);
		if (o == null)
			return ' ';
		if (o instanceof Character) {
			return ((Character) o).charValue();
		} else if (o instanceof String) {
			try {
				return new Character(((String) o).charAt(0)).charValue();
			} catch (Throwable e) {
				throw new RuntimeException(name + " is not a Character, it is '" + o + "'", e);
			}
		} else {
			throw new RuntimeException(name + " is not a Character, it is '" + o + "'");
		}
	}

	public String[] getEntityKeys() {
		String[] ret = null;
		if (values.size() > 0) {
			Set<String> keySet = values.keySet();
			ret = new String[keySet.size()];
			keySet.toArray(ret);
		}

		return ret;
	}

	public String[] getColumnNames() {
		if (columns == null || columns.length == 0) {
			Set<String> keySet = values.keySet();
			for (int i = 0; i < ignoreColumnName.size(); ++i) {
				keySet.remove(ignoreColumnName.get(i));
			}
			columns = new String[keySet.size()];
			keySet.toArray(columns);
		}
		Arrays.sort(columns);
		return columns;
	}

	public int getSQLType(String columnName) {
		Object o = get(columnName);
		if (o instanceof String) {
			return Types.VARCHAR;
		} else if (o instanceof String) {
			return Types.VARCHAR;
		} else if (o instanceof Integer) {
			return Types.INTEGER;
		} else if (o instanceof Long) {
			return Types.BIGINT;
		} else if (o instanceof Short) {
			return Types.SMALLINT;
		} else if (o instanceof Float) {
			return Types.FLOAT;
		} else if (o instanceof Double) {
			return Types.DOUBLE;
		} else if (o instanceof Byte) {
			return Types.TINYINT;
		} else if (o instanceof Boolean) {
			return Types.BOOLEAN;
		} else if (o instanceof Date) {
			return Types.DATE;
		} else if (o instanceof Timestamp) {
			return Types.TIMESTAMP;
		} else {
			return Types.VARCHAR;
		}
	}

	public String[] getCompareColumns() {
		return compareColumn;
	}

	public abstract BaseEntity getDefaultEmptyBean();

	public double getDouble(String name) {
		Object o = getNumber(name);
		if (o == null)
			return 0;
		return ((Number) o).doubleValue();
	}

	public double[] getDoubleArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new double[0];
		return ArrayUtil.parseToDoubleArray((Number[]) o);
	}

	public Double[] getDoubleObjectArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new Double[0];
		return ArrayUtil.parseToDoubleObjectArray((Number[]) o);
	}

	public float getFloat(String name) {
		Object o = getNumber(name);
		if (o == null)
			return 0;
		return ((Number) o).floatValue();
	}

	public float[] getFloatArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new float[0];
		return ArrayUtil.parseToFloatArray((Number[]) o);
	}

	public Float[] getFloatObjectArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new Float[0];
		return ArrayUtil.parseToFloatObjectArray((Number[]) o);
	}

	public int getInt(String name) {
		Object o = getNumber(name);
		if (o == null)
			return 0;
		return ((Number) o).intValue();
	}

	public int[] getIntArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new int[0];
		return ArrayUtil.parseToIntArray((Number[]) o);
	}

	public Integer[] getIntObjectArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new Integer[0];
		return ArrayUtil.parseToIntObjectArray((Number[]) o);
	}

	public Timestamp getTimestamp(String name) {
		Object o = get(name);
		if (o == null)
			return null;
		if (o instanceof Timestamp) {
			return ((Timestamp) o);
		} else if (o instanceof String) {
			return DateUtil.format((String) o);
		} else {
			throw new RuntimeException(name + " is not a Timestamp, it is '" + o + "'");
		}
	}

	public void setDateFormat(String name, String format) {
		if (!(get(name) instanceof Date)) {
			throw new IllegalArgumentException(name + " is not java.util.Date Object");
		}
		String columnName = (name + DATE_FORMAT_TAG).toLowerCase();
		setString(columnName, format, Integer.MAX_VALUE);
		ignoreColumnName.add(columnName);
	}

	private ArrayList<String> ignoreColumnName = new ArrayList<String>();

	public long getLong(String name) {
		Object o = getNumber(name);
		if (o == null)
			return 0;
		return ((Number) o).longValue();
	}

	public long[] getLongArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new long[0];
		return ArrayUtil.parseToLongArray((Number[]) o);
	}

	public Long[] getLongObjectArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new Long[0];
		return ArrayUtil.parseToLongObjectArray((Number[]) o);
	}

	private Object getNullValue(Object o) {
		if (o instanceof Integer) {
			return Integer_ZERO;
		}
		if (o instanceof Long) {
			return Long_ZERO;
		}
		if (o instanceof Double) {
			return Double_ZERO;
		}
		if (o instanceof Float) {
			return Float_ZERO;
		}
		if (o instanceof Byte) {
			return Byte_ZERO;
		}
		if (o instanceof Short) {
			return Short_ZERO;
		}
		if (o instanceof Character) {
			return Character_SPACE;
		}
		if (o instanceof String) {
			return String_Empty;
		}
		return null;
	}

	public short getShort(String name) {
		Object o = getNumber(name);
		if (o == null)
			return 0;
		return ((Number) o).shortValue();
	}

	public short[] getShortArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new short[0];
		return ArrayUtil.parseToShortArray((Number[]) o);
	}

	public Short[] getShortObjectArray(String name) {
		Object o = getNumberArray(name);
		if (o == null)
			return new Short[0];
		return ArrayUtil.parseToShortObjectArray((Number[]) o);
	}

	public String getString(String name) {
		Object o = get(name);
		if (o == null)
			return "";
		if (o instanceof String) {
			return (String) o;
		} else if (o instanceof Date) {
			// String format = getString(name + DATE_FORMAT_TAG);
			// if (format == null || format.trim().length() == 0) {
			return DateUtil.formatTimestamp((Date) o);
			// } else {
			// try {
			// return DateUtil.format((Date) o, format);
			// } catch (Exception e) {
			// return DateUtil.formatTimestamp((Date) o);
			// }
			// }
		} else {
			return ObjectUtils.toString(o);
		}
	}

	public abstract String getTheEntityName();

	public Map<String, Object> getValueMap() {
		return values;
	}

	/**
	 * 获取属性值map
	 * 
	 * @param removeKey
	 *            (需要移除的字段key)
	 * @return
	 */
	public Map<String, Object> getFieldsMap(String removeKey) {
		Map<String, Object> copy = new HashMap<String, Object>();
		copy.putAll(values);
		if (copy != null) {
			if (copy.containsKey(removeKey)) {
				copy.remove(removeKey);
			}
		}
		return copy;
	}

	public void setValueMap(Map<String, Object> valueMap) {
		if (valueMap == null)
			return;
		Iterator<String> keyIterator = valueMap.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			values.put(key.toLowerCase(), valueMap.get(key));
		}
	}

	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder(-426830461, 631494429);
		Iterator<String> keyIterator = values.keySet().iterator();
		while (keyIterator.hasNext()) {
			builder.append(values.get(keyIterator.next()));
		}
		return builder.toHashCode();
	}

	public boolean isSortColumn(String columnName) {
		if (columnName == null)
			return false;
		for (int i = 0; i < compareColumn.length; ++i) {
			if (columnName.equalsIgnoreCase(compareColumn[i]))
				return true;
		}
		return false;
	}

	protected Map<String, Object> newMap() {
		return new HashMap<String, Object>();
	}

	public void reset() {
		Iterator<String> keyIterator = values.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			Object nullValue = getNullValue(values.get(key));
			if (nullValue == null) {
				values.remove(key);
			} else {
				values.put(key, nullValue);
			}
		}
	}

	public void set(String name, boolean value) {
		set(name, new Boolean(value));
	}

	public void set(String name, byte value) {
		set(name, new Byte(value));
	}

	public void set(String name, char value) {
		set(name, new Character(value));
	}

	public void set(String name, double value) {
		set(name, new Double(value));
	}

	public void set(String name, float value) {
		set(name, new Float(value));
	}

	public void set(String name, int value) {
		set(name, new Integer(value));
	}

	public void set(String name, long value) {
		set(name, new Long(value));
	}

	public void set(String name, Object value) {
		setNotParseName(name, value);
	}

	public void set(String name, short value) {
		set(name, new Short(value));
	}

	public void setCompareColumns(String[] compareColumns) {
		if (compareColumns == null || compareColumns.length == 0)
			return;
		compareColumn = compareColumns;
	}

	public void setString(String name, String value, int maxLength) {
		set(name, StringUtils.substring(value, 0, maxLength));
	}

	public String toDataString() {
		Set<String> keySet = values.keySet();
		String[] keys = new String[keySet.size()];
		keySet.toArray(keys);
		Arrays.sort(keys);
		StringBuffer sb = new StringBuffer("data: {");
		for (int i = 0; i < keys.length; ++i) {
			if (i > 0)
				sb.append(", ");
			sb.append(keys[i]);
			sb.append(":");
			sb.append(toString(values.get(keys[i])));
		}
		sb.append("}");
		return sb.toString();
		// return values.toString();
	}

	public String toJSONString() {
		return this.toJSONString(true);
	}

	public String toJSONString(boolean recordDate) {
		JSONObject obj = new JSONObject();
		Iterator<String> keyIterator = values.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = (String) keyIterator.next();
			Object o = values.get(key);
			if (o instanceof Boolean) {
				obj.put(key, ((Boolean) o).booleanValue());
			} else {
				String str = "";
				if (o instanceof Date) {
					obj.put(key, ((Date) o).getTime());
					continue;
				} else if (o instanceof Number) {
					obj.put(key, o);
					continue;
				} else {
					str = StringUtil.codeReplace(o.toString());
				}
				try {
					obj.put(key, str);
				} catch (Throwable e) {
				}
			}
		}
		if (recordDate) {
			obj.put("record_create_time", new Date().getTime());
		}

		String recordJson = obj.toString();
		
		JSONObject recordObj = new JSONObject();
		recordObj.put("type", getTheEntityName().toLowerCase());
		recordObj.put("record", recordJson);
		
		return recordObj.toString();
	}

	public String toMongoJSONString() {

		JSONObject obj = new JSONObject();
		Iterator<String> keyIterator = values.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = (String) keyIterator.next();
			Object o = values.get(key);
			if (o instanceof Boolean) {
				obj.put(key, ((Boolean) o).booleanValue());
			} else {
				String str = "";
				if (o instanceof Date) {
					// 日期格式使用DC_StoreService中定义的格式
					obj.put(key, DateUtil.format((Date) o, "yyyy/MM/dd HH:mm:ss.SSS"));
					continue;
				} else if (o instanceof Number) {
					obj.put(key, o);
					continue;
				} else {
					str = StringUtil.codeReplace(o.toString());
				}
				try {
					if (key.equalsIgnoreCase("RefindKey")) {
						obj.put("_id", str);
					} else {
						obj.put(key, str);
					}
				} catch (Throwable e) {
				}
			}
		}
		obj.put("record_create_time", DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss.SSS"));
		return obj.toString();
	}

	public String toXMLString() {
		StringBuffer ret = new StringBuffer("<");
		ret.append(getTheEntityName().toLowerCase());
		ret.append(">");
		String[] keys = new String[values.size()];
		values.keySet().toArray(keys);
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; ++i) {
			String key = keys[i];
			boolean makeNode = true;
			if (key.endsWith(DATE_FORMAT_TAG)) {
				String dataKey = key.substring(0, key.length() - DATE_FORMAT_TAG.length());
				if (values.containsKey(dataKey)) {
					makeNode = false;
				}
			}
			if (makeNode) {
				Object o = values.get(key);
				ret.append("<");
				ret.append(key);
				ret.append(" type=\"");
				String type = getType(o);
				ret.append(type);
				if (type.equals("Date")) {
					String dateformat = getString(key + DATE_FORMAT_TAG).trim();

					if (dateformat != null && dateformat.length() > 0 && !"undefined".equalsIgnoreCase(dateformat)) {

						ret.append("\" format=\"");
						ret.append(dateformat);

						o = DateUtil.formatTimestamp((Date) o);
					}
				}
				ret.append("\">");
				ret.append(XMLUtil.parseXMLValue(o));
				ret.append("</");
				ret.append(key);
				ret.append(">");
			}
		}
		ret.append("</");
		ret.append(getTheEntityName().toLowerCase());
		ret.append(">");
		return ret.toString();

	}

	private String getType(Object o) {
		if (o instanceof BaseEntity) {
			return "BaseEntity";
		}
		if (o instanceof Date) {
			return "Date";
		}
		if (o instanceof String) {
			return "String";
		}

		return o.getClass().getName();
	}

	private String toString(Object o) {
		if (o == null)
			return null;
		if (ArrayUtil.isObjectArray(o)) {
			return ArrayUtil.toString((Object[]) o);
		} else if (ArrayUtil.isLightObjectArray(o)) {
			if ((o instanceof long[])) {
				return ArrayUtil.toString((long[]) o);
			} else if ((o instanceof int[])) {
				return ArrayUtil.toString((int[]) o);
			} else if ((o instanceof short[])) {
				return ArrayUtil.toString((short[]) o);
			} else if ((o instanceof double[])) {
				return ArrayUtil.toString((double[]) o);
			} else if ((o instanceof float[])) {
				return ArrayUtil.toString((float[]) o);
			} else if ((o instanceof char[])) {
				return ArrayUtil.toString((char[]) o);
			} else if ((o instanceof byte[])) {
				return ArrayUtil.toString((byte[]) o);
			} else if ((o instanceof boolean[])) {
				return ArrayUtil.toString((boolean[]) o);
			} else {
				return o.toString() + "{}";
			}
		} else {
			return o.toString();
		}
	}

	protected abstract String[] initColumns();

	protected abstract String[] initCompareColumns();

	@Override
	public String toString() {
		return super.toString() + "\t" + getTheEntityName() + ": " + toDataString();
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public boolean isSubEntity(BaseEntity entity) {
		if (entity == null)
			return false;

		if (getTheEntityName() != null && !getTheEntityName().equals(entity.getTheEntityName())) {
			return false;
		}

		String[] columnNames = getColumnNames();
		if (columnNames != null && columns.length > 0) {
			String[] compareEntityColumnNames = entity.getColumnNames();
			for (int i = 0; i < compareEntityColumnNames.length; ++i) {
				boolean isInclude = false;
				for (int j = 0; j < columnNames.length && !isInclude; ++j) {
					if (compareEntityColumnNames[i].equals(columnNames[i])) {
						isInclude = true;
					}
				}
				if (!isInclude) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 获取RefindKey
	 * 
	 * @return
	 */
	public String getRefindKey() {
		return contains("_id") ? getString("_id") : getString("refindkey");
	}
}
