package org.mozilla.javascript.xmlimpl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeFunctionUtil;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.NativeObjectUtil;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

import com.cic.datacrawl.core.util.ArrayUtil;
import com.cic.datacrawl.core.util.DateUtil;
import com.cic.datacrawl.core.util.XMLUtil;

public class ObjectUtils {
	private static final Logger logger = Logger.getLogger(ObjectUtils.class);

	public static final String EMPTY_XML_OBJECT = "An Empty XMLList";
	public static final String NULL_OBJECT = "null";
	public static final String UNDEFINED_OBJECT = "undefined";

	public static String toString(Object obj) {
		if (obj == null) {
			return NULL_OBJECT;
		} else if (obj instanceof Undefined) {
			return UNDEFINED_OBJECT;
		} else if (obj.getClass().isArray()) {
			String type = obj.getClass().getSimpleName();
			if ("int[]".equals(type)) {
				return ArrayUtil.toString((int[]) obj);
			} else if ("long[]".equals(type)) {
				return ArrayUtil.toString((long[]) obj);
			} else if ("short[]".equals(type)) {
				return ArrayUtil.toString((short[]) obj);
			} else if ("boolean[]".equals(type)) {
				return ArrayUtil.toString((boolean[]) obj);
			} else if ("double[]".equals(type)) {
				return ArrayUtil.toString((double[]) obj);
			} else if ("float[]".equals(type)) {
				return ArrayUtil.toString((float[]) obj);
			} else if ("char[]".equals(type)) {
				return ArrayUtil.toString((char[]) obj);
			} else if ("byte[]".equals(type)) {
				return ArrayUtil.toString((byte[]) obj);
			} else {
				return toString((Object[]) obj);
			}
		} else if (obj instanceof Date) {
			return DateUtil.formatTimestamp((Date) obj);
		} else if (obj instanceof String) {
			return toString((String) obj);
		} else if (obj instanceof Number) {
			long longValue = ((Number) obj).longValue();
			double doubleValue = ((Number) obj).doubleValue();
			if (longValue == doubleValue)
				return String.valueOf(longValue);
			else
				return String.valueOf(doubleValue);
		} else if (obj instanceof XML) {
			return toString((XML) obj);
		} else if (obj instanceof XMLList) {
			return toString((XMLList) obj);
		} else if (obj instanceof NativeJavaArray) {
			return toString((NativeJavaArray) obj);
		} else if (obj instanceof Wrapper) {
			return toString((Wrapper) obj);
		} else if (obj instanceof NativeFunction) {
			return NativeFunctionUtil.toString((NativeFunction) obj);
		} else if (obj instanceof IdScriptableObject) {
			return toString((IdScriptableObject) obj);
		}
		return obj.toString();
	}

	private static String toString(Object[] obj) {
		StringBuilder sb = new StringBuilder(obj.getClass().getName());
		sb.append("[] {");
		for (int i = 0; i < obj.length; ++i) {
			if (i > 0)
				sb.append(", ");
			sb.append(toString(obj[i]));
		}

		sb.append("}");
		return sb.toString();
	}

	private static String toString(IdScriptableObject obj) {
		return NativeObjectUtil.jsObjectToString(obj);
	}

	private static String toString(Wrapper obj) {
		return obj.unwrap().toString();
	}

	private static String toString(NativeJavaArray obj) {
		return toString(obj.unwrap());
	}

	private static String toString(XML xml) {
		return XMLUtil.replaceEasyReadChar(xml.toXMLString());
	}

	private static String toString(String str) {
		if (str.length() == 0) {
			return "";
		}
		return str;
	}

	private static String toString(XMLList xmlList) {
		int length = xmlList.length();
		if (length == 0) {
			return EMPTY_XML_OBJECT;
		}
		return XMLUtil.replaceEasyReadChar(xmlList.toXMLString());

		// StringBuffer sb = new StringBuffer(xmlList.getClassName());
		// sb.append("size: ");
		// sb.append(length);
		// sb.append(" {");
		// for (int i = 0; i < length; ++i) {
		// if (i > 0)
		// sb.append(", ");
		// sb.append(xmlList.child(i).toXMLString());
		// }
		// sb.append("}");
		// return "";
	}

	// public static void main(String[] args) {
	// // Object[] o = new Object[] { null, "", new String("sss") };
	// // for (int i = 0; i < o.length; ++i) {
	// // System.out.println(toString(o[i]));
	// // }
	// long v1 = 123;
	// double d1 = 123.0;
	// long v2 = 234;
	// double d2 = 234.00001;
	// System.out.println(v1 == d1);
	// System.out.println(v2 == d2);
	// }
}
