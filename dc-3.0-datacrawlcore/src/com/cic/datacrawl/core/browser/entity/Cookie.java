package com.cic.datacrawl.core.browser.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.cic.datacrawl.core.util.DateUtil;
import com.cic.datacrawl.core.util.StringUtil;

public class Cookie {

	private String domain;
	private String path;
	private String name;
	private String value;
	private Date expiryDate;

	private HashMap<String, String> valuesMap = new HashMap<String, String>();

	public String[] getAllCustomNames() {
		Set<String> keySet = valuesMap.keySet();
		String[] ret = new String[keySet.size()];
		keySet.toArray(ret);
		return ret;
	}

	public void setValue(String name, String value) {
		if (name == null)
			return;
		if (name.equalsIgnoreCase("domain")) {
			setDomain(value);
		} else if (name.equalsIgnoreCase("name")) {
			setName(value);
		} else if (name.equalsIgnoreCase("value")) {
			setValue(value);
		} else if (name.equalsIgnoreCase("path")) {
			setPath(value);
		} else {
			if (value == null) {
				valuesMap.remove(name);
			} else {
				valuesMap.put(name, value);
			}
		}
	}

	public String getValue(String name) {
		if (!valuesMap.containsKey(name))
			return null;
		return valuesMap.get(name);
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/*
	 * complies with RFC 2109. The value is passed through to the native browser
	 * unchanged. Example value strings: foo=bar (basic session cookie) foo=bar;
	 * path=/; domain=.eclipse.org (session cookie) foo=bar; expires=Thu,
	 * 01-Jan-2030 00:00:01 GMT (persistent cookie) foo=; expires=Thu,
	 * 01-Jan-1970 00:00:01 GMT (deletes cookie foo)
	 * 
	 * Parameters: value the cookie value url the URL to associate the cookie
	 * with Returns: true if the cookie was successfully set and false otherwise
	 * Throws: IllegalArgumentException - ERROR_NULL_ARGUMENT - if the value is
	 * null ERROR_NULL_ARGUMENT - if the url is null Since: 3.5
	 */
	public String toCookieString() {
		StringBuilder cookiesString = new StringBuilder();

		// Name
		if (StringUtil.isEmpty(getName())) {
			cookiesString.append("name=");
		} else {
			cookiesString.append(getName());
			cookiesString.append("=");
		}
		// Value
		if (!StringUtil.isEmpty(getValue())) {
			cookiesString.append(getValue());
		}
		// Domain
		if (StringUtil.isEmpty(getDomain())) {
			cookiesString.append("; ");
			cookiesString.append("domain=");
		} else {
			cookiesString.append("; ");
			cookiesString.append("domain=");
			cookiesString.append(getDomain());
		}
		// Path
		if (StringUtil.isEmpty(getPath())) {
			cookiesString.append("; ");
			cookiesString.append("path=/");
		} else {
			cookiesString.append("; ");
			cookiesString.append("path=");
			cookiesString.append(getPath());
		}
		// ExpiryDate
		if (getExpiryDate() == null) {
			cookiesString.append("; ");
			cookiesString.append("expires=");
		} else {
			cookiesString.append("; ");
			cookiesString.append("expires=");
			cookiesString.append(DateUtil.formatGMTString(getExpiryDate()));
		}

		if (valuesMap.size() > 0) {
			Iterator<String> keyIterator = valuesMap.keySet().iterator();
			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				cookiesString.append("; ");
				cookiesString.append(key);
				cookiesString.append("=");
				cookiesString.append(getValue(key));
			}
		}
		return cookiesString.toString();
	}

	@Override
	public String toString() {
		return super.toString()
				+ "Cookie [domain="
				+ domain
				+ ", expiryDate="
				+ expiryDate
				+ ", name="
				+ name
				+ ", path="
				+ path
				+ ", value="
				+ value
				+ "]";
	}
}
