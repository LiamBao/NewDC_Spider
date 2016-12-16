package com.cic.datacrawl.core.util;

public class ExceptionUtil {
	
	public static boolean isSameException(Throwable throwable, Class<?> clazz) {
		boolean ret = false;

		if (throwable != null) {
			if (throwable.getClass().equals(clazz)) {
				ret = true;
			} else {
				Throwable t = throwable.getCause();
				if (t != null)
					ret = isSameException(t, clazz);
			}
		}

		return ret;
	}
	
}
