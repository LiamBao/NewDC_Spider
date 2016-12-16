package com.cic.datacrawl.core.jsfunction;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObjectUtil;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.xmlimpl.ObjectUtils;
import org.mozilla.javascript.xmlimpl.RhinoXmlUtil;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.ProcessMonitor;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.mail.MailSender;
import com.cic.datacrawl.core.rhino.RhinoContext;
import com.cic.datacrawl.core.rhino.VerifyCodeInputer;
import com.cic.datacrawl.core.rhino.shell.Runner;
import com.cic.datacrawl.core.system.SystemInterface;
import com.cic.datacrawl.core.util.DateUtil;
import com.cic.datacrawl.core.util.FileUtils;

/**
 * 支持Rhino所需的全局系统函数
 */
public final class RhinoStandardFunction {
	private final static Logger LOG = Logger.getLogger(RhinoStandardFunction.class);

	public static void exit(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		int exitCode = 0;
		if (args.length > 1) {
			try {
				String exitMessage = ObjectUtils.toString(args[0]);
				LOG.info("exitMessage: " + exitMessage);
				exitCode = new Double(exitMessage).intValue();
			} catch (Exception e) {
			}
		}
		final int finalExitCode = exitCode;
		RhinoEntityFunction.commit(cx, thisObj, args, funObj);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				((SystemInterface) ApplicationContext.getInstance().getBean("systemInterface"))
						.exit(finalExitCode);
			}
		}).start();
	}

	public static void finished(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		ProcessMonitor.finished();
	}

	public static String nodeType(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		return RhinoXmlUtil.getNodeType(args[0]);
	}

	/**
	 * 
	 * @param failed
	 *            Code
	 */
	public static void addDateFormat(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args.length < 1) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in addDateFormat(dateFormat)", "", 0);
			throw error;
		}

		String dateformat = ObjectUtils.toString(args[0]);
		DateUtil.addDateFormat(dateformat);
	}

	/**
	 * 
	 * @param failed
	 *            Code
	 */
	public static void failed(Context cx, Scriptable thisObj, Object[] args, Function funObj) {

		if (args.length < 1) {
			JavaScriptException error = new JavaScriptException("Illegal Argument in failed(failed code)",
					"", 0);
			throw error;
		}

		int status = 0;
		try {
			status = Integer.parseInt(ObjectUtils.toString(args[0]));
		} catch (Exception e) {
			JavaScriptException error = new JavaScriptException("Illegal Argument in failed(failed code)",
					"", 0);
			throw error;
		}

		if(args.length <= 2) {
			ProcessMonitor.failed(status, ObjectUtils.toString(args[1]));
		} else {
			ProcessMonitor.failed(status, ObjectUtils.toString(args[1]), ObjectUtils.toString(args[2]));
		}
	}

	/**
	 * 
	 * @param subject
	 * @param content
	 * @param ToAddress
	 * @param type
	 *            default: text
	 */
	public static void sendMail(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args.length < 3) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in sendMail(subject, content, to)", "", 0);
			throw error;
		}
		String subject = ObjectUtils.toString(args[0]);
		String content = ObjectUtils.toString(args[1]);
		String to = ObjectUtils.toString(args[2]);
		String type = "text";
		if (args.length >= 4) {
			type = ObjectUtils.toString(args[3]).toLowerCase();
		}

		com.cic.datacrawl.core.mail.MailConfiguration mailConfig = (com.cic.datacrawl.core.mail.MailConfiguration) ApplicationContext
				.getInstance().getBean("mailConfig");

		if ("text".equalsIgnoreCase(type)) {
			mailConfig.setTextFormat("text/plain");
		} else if ("xml".equalsIgnoreCase(type)) {
			mailConfig.setTextFormat("text/xml");
		} else if ("html".equalsIgnoreCase(type)) {
			mailConfig.setTextFormat("text/html");
		} else {
			mailConfig.setTextFormat("text/plain");
		}
		MailSender.sendMessage(to, subject, content, mailConfig);
	}

	public static String getLocalHostname() {
		String ret = "";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ret = addr.getHostName();// 获得本机名称

		} catch (UnknownHostException e) {
		}
		return ret;
	}

	public static String getLocalIp() {
		String ret = "";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ret = addr.getHostAddress();

		} catch (UnknownHostException e) {
		}
		return ret;
	}

	/**
	 * Print the string values of its arguments.
	 * 
	 * This method is defined as a JavaScript function. Note that its arguments
	 * are of the "varargs" form, which allows it to handle an arbitrary number
	 * of arguments supplied to the JavaScript function.
	 * 
	 */
	public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		PrintStream out = System.out;
		for (int i = 0; i < args.length; i++) {
			// if (i > 0)
			// out.print(" ");

			// Convert the arbitrary JavaScript value into a string form.
			String s = ObjectUtils.toString(args[i]);

			out.print(s);
		}
		// out.println();
	}

	/**
	 * 打印输出并换行
	 * 
	 * @param o
	 */
	public static void println(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		PrintStream out = System.out;
		if (args.length == 0) {
			out.println();
		} else {
			for (int i = 0; i < args.length; i++) {
				// if (i > 0)
				// out.println();

				// Convert the arbitrary JavaScript value into a string form.
				String s = ObjectUtils.toString(args[i]);

				out.println(s);
			}
		}
	}

	public static void setTimeout(final Context cx, final Scriptable thisObj, Object[] args, Function funObj) {
		boolean isInvalidFunction = args == null
									|| args.length != 2
									|| (!(args[0] instanceof Function) && !(args[0] instanceof Script));

		long timeout = Long.MIN_VALUE;
		if (!isInvalidFunction) {
			try {
				timeout = new Double(ObjectUtils.toString(args[1])).longValue();
				if (timeout < 0) {
					LOG.error("IllegalArgumentException: Timeout must be bigger than ZERO.");
					return;
				}
			} catch (Exception e) {
			}

		}
		isInvalidFunction = isInvalidFunction || timeout < 0;
		if (isInvalidFunction) {
			StringBuilder sb = new StringBuilder("IllegalFunctionException: setTimeout(");
			for (int i = 0; i < args.length; ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				if (args[i] instanceof ScriptableObject) {
					sb.append(((ScriptableObject) args[i]).getClassName());
				} else {
					sb.append(args[i].getClass().getName());
				}
			}
			sb.append(") is not defined.");
			LOG.error(sb.toString());
			return;
		}

		Scriptable scope = funObj.getParentScope();
		Runner runner = null;
		if (args[0] instanceof Function) {
			Object[] newArgs = ScriptRuntime.emptyArgs;
			runner = new Runner(scope, (Function) args[0], newArgs);
		} else {
			runner = new Runner(scope, (Script) args[0]);
		}
		runner.setFactory(cx.getFactory());
		runner.setWaitTime(timeout);
		runner.setExecuteCount(1);
		Thread thread = new Thread(runner);
		thread.start();
	}

	public static Runner setInterval(final Context cx, final Scriptable thisObj, Object[] args,
			Function funObj) {
		boolean isInvalidFunction = args == null || args.length != 2 || !(args[0] instanceof Function);

		long timeout = Long.MIN_VALUE;
		if (!isInvalidFunction) {
			try {
				timeout = new Double(ObjectUtils.toString(args[1])).longValue();
				if (timeout < 0) {
					LOG.error("IllegalArgumentException: Waittime must be bigger than ZERO.");
					return null;
				}
			} catch (Exception e) {
			}

		}
		isInvalidFunction = isInvalidFunction || timeout < 0;
		if (isInvalidFunction) {
			StringBuilder sb = new StringBuilder("IllegalFunctionException: setInterval(");
			for (int i = 0; i < args.length; ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				if (args[i] instanceof ScriptableObject) {
					sb.append(((ScriptableObject) args[i]).getClassName());
				} else {
					sb.append(args[i].getClass().getName());
				}
			}
			sb.append(") is not defined.");
			LOG.error(sb.toString());
			return null;
		}
		Scriptable scope = funObj.getParentScope();
		Runner runner = null;
		if (args[0] instanceof Function) {
			Object[] newArgs = ScriptRuntime.emptyArgs;
			runner = new Runner(scope, (Function) args[0], newArgs);
		} else {
			runner = new Runner(scope, (Script) args[0]);
		}
		runner.setFactory(cx.getFactory());
		runner.setWaitTime(timeout);
		Thread thread = new Thread(runner);
		thread.start();
		return runner;
	}

	private static long totalSleepTime;

	public static void resetTotalSleepTime() {
		totalSleepTime = 0;
	}

	public static long getTotalSleepTime() {
		return totalSleepTime;
	}

	/**
	 * 系统休眠(等待)x毫秒
	 * 
	 * @param millis
	 */
	public static void sleep(final Context cx, final Scriptable thisObj, Object[] args, Function funObj) {
		long millis = 0;
		if (args.length > 0) {
			if (args[0] instanceof Number) {
				millis = ((Number) args[0]).longValue();
			} else {
				try {
					millis = Long.parseLong(ObjectUtils.toString(args[0]));
				} catch (Exception e) {
				}
			}
		}

		if (millis > 0) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			totalSleepTime += millis;
		}
	}

	public static Timestamp getCurrentTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static boolean isDate(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException("Illegal Argument in isDate(date)", "", 0);
			throw error;
		}
		Object obj = NativeObjectUtil.jsObject2java(args[0]);
		if (obj == null) {
			return false;
		}
		return (obj instanceof Date);
	}

	public static int calcDays(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		Date date = null;
		if (!isDate(cx, thisObj, args, funObj)) {
			date = RhinoConvertFunction.parseTimestamp(cx, thisObj, args, funObj);

		} else {
			date = (Date) NativeObjectUtil.jsObject2java(args[0]);
		}
		if (date == null) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in calcDays(startDate, format, timezone)", "", 0);
			throw error;
		}

		return DateUtil.calcDays(date);
	}

	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

	public static String getSystemProperty(String key) {
		return System.getProperty(key);
	}

	// 验证码输入支持
	public static String inputVerifyCode(Object oImage) {
		if (verifyCodeInputer == null)
			throw new RuntimeException("Cann't support showInputBox now.");

		Image image = (Image) ((NativeJavaObject) oImage).unwrap();
		String retValue = verifyCodeInputer.showInput(image);
		return retValue;
	}

	public static boolean isEditMode(){
		return Config.getInstance().isEditMode();
	}
	
	public static VerifyCodeInputer verifyCodeInputer;

	public static RhinoContext rhinoContext;

	/**
	 * 执行其他的js脚本
	 * 
	 * @param filename
	 */
	public static void load(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args.length > 0) {
			String jsFileName = (String) args[0];

			File f = new File(jsFileName);
			String oldJSFolder = Config.getJSFolder();
			if (!f.exists()) {
				jsFileName = FileUtils.buildAbsolutelyPath(Config.getJSFolder(), jsFileName);
				f = new File(jsFileName);
				if (!f.exists()) {
					String errorMessage = "File is not exist.(" + (String) args[0] + ")";
					LOG.error(errorMessage);
					throw new RuntimeException(errorMessage);
				}
			}
			String script;

			String jsFolder = f.getParentFile().getAbsolutePath();
			Config.setJSFolder(jsFolder);
			try {				
				script = FileUtils.readFile(f, "UTF-8");

				if (LOG.isDebugEnabled())
					LOG.debug("Load File path: " + jsFileName);

				rhinoContext.execute(script, jsFileName);
			} catch (IOException e) {
				Config.setJSFolder(oldJSFolder);
				LOG.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	}

	public static void gc(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		System.gc();
	}

	public static Object getBean(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException("Illegal Argument in isDate(date)", "", 0);
			throw error;
		}
		try {
			return ApplicationContext.getInstance().getBean(ObjectUtils.toString(args[0]));
		} catch (Exception e) {
			JavaScriptException error = new JavaScriptException(e.getMessage(), "", 0);
			throw error;
		}
	}
}
