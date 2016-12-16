package com.cic.datacrawl.core.jsfunction;

import java.io.PrintStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.ProcessMonitor;
import com.cic.datacrawl.core.browser.RhinoBrowserImpl;

/**
 * 支持Rhino所需的全局函数用来提供记录LOG
 */
public final class RhinoLoggerFunction {
	private final static Logger LOG = Logger.getLogger(RhinoLoggerFunction.class);

	public static boolean isDebugEnabled() {
		return LOG.isDebugEnabled();
	}

	public static boolean isInfoEnabled() {
		return LOG.isInfoEnabled();
	}

	/**
	 * 信息日志记录
	 * 
	 * @param message
	 */
	public static void logDebug(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		if (LOG.isDebugEnabled()) {
			PrintStream out = System.out;
			for (int i = 0; i < args.length; i++) {
				if (i > 0)
					out.print(" ");

				// Convert the arbitrary JavaScript value into a string form.
				String s = ObjectUtils.toString(args[i]);

				LOG.debug(s);
			}
		}
	}

	/**
	 * 信息日志记录
	 * 
	 * @param message
	 */
	public static void logInfo(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		if (LOG.isInfoEnabled()) {
			PrintStream out = System.out;
			for (int i = 0; i < args.length; i++) {
				if (i > 0)
					out.print(" ");

				// Convert the arbitrary JavaScript value into a string form.
				String s = ObjectUtils.toString(args[i]);

				LOG.info(s);
			}
		}
	}

	/**
	 * 错误日志记录
	 * 
	 * @param message
	 */
	public static void logError(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		if (LOG.isEnabledFor(Level.ALL) || LOG.isEnabledFor(Level.ERROR)) {
			PrintStream out = System.out;
			StringBuilder errorMessage = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
				if (i > 0){
					out.print(" ");
					errorMessage.append("\n");
				}

				// Convert the arbitrary JavaScript value into a string form.
				String s = ObjectUtils.toString(args[i]);
				errorMessage.append(s);
				LOG.error(s);
			}
			ProcessMonitor.setScriptErrorMessage(errorMessage.toString());
			ProcessMonitor.setScriptErrorURL(RhinoBrowserImpl.getInstance().getUrl());
		}
	}

	/**
	 * 警告日志记录
	 * 
	 * @param message
	 */
	public static void logWarn(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		if (LOG.isEnabledFor(Level.ALL) || LOG.isEnabledFor(Level.WARN)) {
			PrintStream out = System.out;
			for (int i = 0; i < args.length; i++) {
				if (i > 0)
					out.print(" ");

				// Convert the arbitrary JavaScript value into a string form.
				String s = ObjectUtils.toString(args[i]);

				LOG.warn(s);
			}
		}
	}

}
