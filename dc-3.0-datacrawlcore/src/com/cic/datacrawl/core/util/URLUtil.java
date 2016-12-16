package com.cic.datacrawl.core.util;

import java.net.URL;

public class URLUtil {
	public static String parseDomain(URL url) {
		if (url == null)
			return null;
		return url.getHost();
	}

	public static String mergeURL(String domain, String uri) {
		if (domain == null || domain.trim().length() == 0) {
			throw new RuntimeException("Invalid Argument(domain) in mergeURL(domain, uri)");
		}
		if (uri == null || uri.trim().length() == 0) {
			throw new RuntimeException("Invalid Argument(uri) in mergeURL(domain, uri)");
		}
		domain = domain.trim();
		uri = uri.trim();
		if (uri.indexOf("http://") == 0) {
			return uri;
		} else if(uri.indexOf("https://") == 0){
			return uri;
		}
		if (uri.indexOf(domain) >= 0) {
			if (uri.indexOf("http://") != 0) {
				uri = "http://" + uri;
			}
			return uri;
		}
		if (domain.indexOf("http://") != 0 && domain.indexOf("https://") != 0) {
			domain = "http://" + domain;
		}
		if (domain.charAt(domain.length() - 1) == '/') {
			domain = domain.substring(0, domain.length() - 1);
		}
		if (uri.charAt(0) == '/') {
			uri = uri.substring(1);
		}
		return domain + "/" + uri;
	}

	public static String parseDomain(String url) {
		if (url == null || url.trim().length() == 0)
			return null;
		try {
			return parseDomain(new URL(url));
		} catch (Throwable e) {
			return null;
		}
	}

	public static boolean isValid(String url) {
		try {
			new URL(url);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isDomain(String domain, String url) {
		if (domain == null || domain.length() == 0 || url == null)
			return false;

		return url.indexOf(domain) > 0;
	}

}
