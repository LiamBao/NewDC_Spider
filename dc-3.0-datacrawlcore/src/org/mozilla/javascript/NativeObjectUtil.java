package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.mozilla.javascript.regexp.NativeRegExp;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

public class NativeObjectUtil {

	/**
	 * 根据javascript对象的类型，转换该js对象为相应的Java对象. 按js对象类型分: Wrapper: unwrap为Java对象
	 * 下标数组: 转换为ArrayList 关联数组: 转换为HashMap<String,Object> JSObject:
	 * 转换为HashMap<String,Object> 其它: 保持为原始的rhino对象类型
	 */
	public static Object jsObject2java(Object jsObj) {
		if (jsObj == null || jsObj == Undefined.instance || jsObj == ScriptableObject.NOT_FOUND)
			return null;

		if (jsObj instanceof Wrapper)
			return ((Wrapper) jsObj).unwrap();

		if (jsObj instanceof NativeArray) {
			NativeArray jsArray = (NativeArray) jsObj;
			Object[] jsIds = jsArray.getIds();
			int jsLength = (int) jsArray.getLength();
			if (jsIds.length == 0 || jsLength == jsIds.length) {
				ArrayList<Object> result = new ArrayList<Object>();
				for (int i = 0; i < jsLength; i++)
					result.add(jsObject2java(jsArray.get(i, jsArray)));
				return result;
			} else
				return jsArray2Map(jsArray);
		}

		if (jsObj instanceof NativeFunction)
			return NativeFunctionUtil.toString((NativeFunction) jsObj);

		if (jsObj instanceof NativeDate) {
			NativeDate jsDate = (NativeDate) jsObj;
			return new Date((long) jsDate.getJSTimeValue());
		}
		if (jsObj instanceof NativeBoolean) {
			NativeBoolean jsBoolean = (NativeBoolean) jsObj;
			return jsBoolean.getDefaultValue(ScriptRuntime.BooleanClass);
		}
		if (jsObj instanceof NativeRegExp) {
			NativeRegExp jsRegExp = (NativeRegExp) jsObj;
			return jsRegExp.toString();
		}

		if (jsObj instanceof NativeString) {
			NativeString jsString = (NativeString) jsObj;
			return jsString.toString();
		}

		if (jsObj instanceof NativeNumber) {
			NativeNumber jsNumber = (NativeNumber) jsObj;
			return new Double(jsNumber.toString());
		}

		if (jsObj instanceof NativeError) {
			NativeError jsError = (NativeError) jsObj;
			// [message, fileName, lineNumber, name, rhinoException]
			HashMap<String, Object> errorMessageMap = new HashMap<String, Object>();
			errorMessageMap.put("message", jsError.get("message", jsError));
			errorMessageMap.put("fileName", jsError.get("fileName", jsError));
			errorMessageMap.put("lineNumber", jsError.get("lineNumber", jsError));
			errorMessageMap.put("name", jsError.get("name", jsError));
			return errorMessageMap.toString();
		}
		if (jsObj instanceof NativeObject)
			return jsArray2Map((NativeObject) jsObj);

		return jsObj;
	}

	public static HashMap<String, Object> jsArray2Map(IdScriptableObject scriptObj) {
		final Object[] ids = scriptObj.getIds();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < ids.length; i++) {
			final String key = ids[i].toString();
			final Object value = jsObject2java(scriptObj.get(key, scriptObj));
			map.put(key, jsObject2java(value));
		}
		return map;
	}

	public static NativeArray javaMap2jsArray(HashMap<String, String> map) {
		NativeArray result = new NativeArray(0);
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Object javaVal = map.get(key);

			result.put(key, result, javaVal);
		}
		return result;
	}

	public static String jsObjectToString(IdScriptableObject obj) {
		String ret = "Undefined";
		if (obj == null) {
			return ret;
		} else if (obj instanceof NativeError) {
			ret = toString((NativeError) obj);
		} else if (obj instanceof XMLObject) {
			ret = toString((XMLObject) obj);
		} else if (obj instanceof NativeArray) {
			ret = toString((NativeArray) obj);
		} else if (obj instanceof NativeBoolean) {
			ret = toString((NativeBoolean) obj);
		} else if (obj instanceof NativeDate) {
			ret = toString((NativeDate) obj);
		} else if (obj instanceof NativeNumber) {
			ret = toString((NativeNumber) obj);
		} else if (obj instanceof NativeRegExp) {
			ret = toString((NativeRegExp) obj);
		} else if (obj instanceof NativeString) {
			ret = toString((NativeString) obj);
		} else if (obj instanceof NativeObject) {
			ret = toString((NativeObject) obj);
		} else if (obj instanceof NativeMath) {
			ret = toString((NativeMath) obj);
		} else if (obj instanceof NativeIterator) {
			ret = toString((NativeIterator) obj);
		}
		return ret;

	}

	private static String toString(NativeIterator obj) {
		Object o = NativeObjectUtil.jsObject2java(obj);
		return ObjectUtils.toString(o);
	}

	private static String toString(NativeMath obj) {
		Object o = NativeObjectUtil.jsObject2java(obj);
		return ObjectUtils.toString(o);
	}

	private static String toString(NativeObject obj) {
		Object o = NativeObjectUtil.jsObject2java(obj);
		return ObjectUtils.toString(o);
	}

	private static String toString(NativeString obj) {
		Object o = NativeObjectUtil.jsObject2java(obj);
		return ObjectUtils.toString(o);
	}

	private static String toString(NativeRegExp obj) {
		Object o = NativeObjectUtil.jsObject2java(obj);
		return ObjectUtils.toString(o);
	}

	private static String toString(NativeNumber obj) {
		Object o = NativeObjectUtil.jsObject2java(obj);
		return ObjectUtils.toString(o);
	}

	private static String toString(NativeDate obj) {
		Object o = NativeObjectUtil.jsObject2java(obj);
		return ObjectUtils.toString(o);
	}

	private static String toString(NativeBoolean obj) {
		Object o = NativeObjectUtil.jsObject2java(obj);
		return ObjectUtils.toString(o);
	}

	private static String toString(NativeArray obj) {
		Object o = NativeObjectUtil.jsObject2java(obj);
		return o.toString();
	}

	private static String toString(XMLObject obj) {
		return obj.toString();
	}

	private static String toString(NativeError jsError) {
		StringBuilder ret = new StringBuilder(jsError.toString());		
		if (jsError.has("fileName", jsError)) {
			ret.append(" [");
			ret.append(jsError.get("fileName", jsError));

			if (jsError.has("lineNumber", jsError)) {
				ret.append(": (");
				ret.append(jsError.get("lineNumber", jsError));
				ret.append(")]");
			}
		}
		return ret.toString();
	}
}
