package com.cic.datacrawl.core.jsfunction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.browser.AbstractJavaWebBrowser;
import com.cic.datacrawl.core.browser.entity.Cookie;
import com.cic.datacrawl.core.util.ArrayUtil;
import com.cic.datacrawl.core.util.DateUtil;

public final class RhinoBrowserFunction {
	private static final String COOKIE_NAME = "name";
	private static final String COOKIE_DOMAIN = "domain";
	private static final String COOKIE_EXPIRY_DATE = "expiry_date";
	private static final String COOKIE_PATH = "path";
	private static final String COOKIE_VALUE = "value";

	public static void showAllCookies(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		AbstractJavaWebBrowser browser = AbstractJavaWebBrowser.getCurrentBrowser();
		Cookie[] cookies = browser.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; ++i) {
				System.out.println(cookies[i].toCookieString());
			}
		}
	}

	/**
	 * 获得当前浏览器中的所有COOKIES
	 * 
	 * @param xmlObject
	 *            相应html内容的E4X对象
	 * @return
	 */
	public static NativeArray getCookies(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		AbstractJavaWebBrowser browser = AbstractJavaWebBrowser.getCurrentBrowser();
		Cookie[] cookies = browser.getCookies();
		if (cookies != null && cookies.length > 0) {
			ArrayList<NativeObject> list = new ArrayList<NativeObject>();
			for (int i = 0; i < cookies.length; ++i) {
				Cookie cookie = cookies[i];
				NativeObject obj = new NativeObject();
				obj.put(COOKIE_NAME, obj, cookie.getName());
				obj.put(COOKIE_DOMAIN, obj, cookie.getDomain());
				obj.put(COOKIE_EXPIRY_DATE, obj, new Long(cookie.getExpiryDate().getTime()));
				obj.put(COOKIE_PATH, obj, cookie.getPath());
				obj.put(COOKIE_VALUE, obj, cookie.getValue());
				String[] customKeys = cookie.getAllCustomNames();
				if (customKeys != null) {
					for (int j = 0; j < customKeys.length; ++j) {
						obj.put(customKeys[j], obj, cookie.getValue(customKeys[j]));
					}
				}
				list.add(obj);
			}
			NativeObject[] objects = new NativeObject[list.size()];
			list.toArray(objects);
			return new NativeArray(objects);
		}
		return null;
	}

	/**
	 * 获得当前浏览器中的所有COOKIES
	 * 
	 * @param xmlObject
	 *            相应html内容的E4X对象
	 * @return
	 */
	public static void setCookies(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0){
			JavaScriptException error = new JavaScriptException("Illegal Argument in setCookies(cookies)", "", 0);
			throw error;
		}

		ArrayList<Cookie> arrayList = new ArrayList<Cookie>();
		for (int i = 0; i < args.length; ++i) {
			if (args[i] != null && !args[i].equals(Undefined.instance)) {
				if (args[i] instanceof String) {
					AbstractJavaWebBrowser.getCurrentBrowser().addCookie((String) args[i]);
				} else {
					arrayList.addAll(convertToCookieArray(cx, thisObj, args[i], funObj));
				}
			}
		}
		Cookie[] cookies = new Cookie[arrayList.size()];
		arrayList.toArray(cookies);
		if (cookies != null && cookies.length > 0) {
			AbstractJavaWebBrowser browser = AbstractJavaWebBrowser.getCurrentBrowser();
			browser.addCookies(cookies);
		}
	}

	private static ArrayList<Cookie> convertToCookieArray(Context cx, Scriptable thisObj, Object args,
			Function funObj) {
		if (args == null)
			return new ArrayList<Cookie>();

		ArrayList<Cookie> arrayList = new ArrayList<Cookie>();
		if (args instanceof List) {
			arrayList.addAll(convertToCookieArray(cx, thisObj, args, funObj));
		} else if (args instanceof Cookie[]) {
			Cookie[] argumentCookies = (Cookie[]) args;
			for (int j = 0; j < argumentCookies.length; ++j) {
				arrayList.add(argumentCookies[j]);
			}
		} else if (args instanceof Cookie) {
			Cookie argumentCookie = (Cookie) args;
			arrayList.add(argumentCookie);
		} else if (args instanceof NativeObject) {
			NativeObject obj = (NativeObject) args;

			Cookie cookie = new Cookie();
			cookie.setName(ObjectUtils.toString(obj.get(COOKIE_NAME, thisObj)));
			cookie.setDomain(ObjectUtils.toString(obj.get(COOKIE_DOMAIN, thisObj)));
			Object expDateObj = obj.get(COOKIE_EXPIRY_DATE, thisObj);
			if (expDateObj instanceof Number) {
				cookie.setExpiryDate(new Date(((Number) expDateObj).longValue()));
			} else {
				String expDateStr = ObjectUtils.toString(expDateObj);
				try {
					cookie.setExpiryDate(new Date(Long.parseLong(expDateStr)));
				} catch (NumberFormatException e) {
					cookie.setExpiryDate(DateUtil.format(expDateStr));
				}
			}
			cookie.setPath(ObjectUtils.toString(obj.get(COOKIE_PATH, thisObj)));
			cookie.setValue(ObjectUtils.toString(obj.get(COOKIE_VALUE, thisObj)));

			// Custom Attribute

			Object[] allIds = obj.getAllIds();
			if (allIds != null) {
				String[] protectedKeys = new String[] { COOKIE_NAME, COOKIE_DOMAIN, COOKIE_VALUE,
						COOKIE_EXPIRY_DATE, COOKIE_PATH };
				for (int i = 0; i < allIds.length; ++i) {
					String key = ObjectUtils.toString(allIds[i]);
					if (!ArrayUtil.content(protectedKeys, key)) {
						cookie.setValue(key, ObjectUtils.toString(obj.get(key, thisObj)));
					}
				}
			}

			arrayList.add(cookie);
		} else if (args instanceof NativeArray) {
			NativeArray array = (NativeArray) args;
			for (int j = 0; j < array.getLength(); ++j) {
				arrayList.addAll(convertToCookieArray(cx, thisObj, array.get(j, thisObj), funObj));
			}
		}
		return arrayList;
	}
}
