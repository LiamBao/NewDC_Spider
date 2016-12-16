package com.cic.datacrawl.management.rhino;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.jsfunction.RhinoStandardFunction;
import com.cic.datacrawl.core.system.SystemInterface;
import com.cic.datacrawl.management.config.LocaleControlConfig;

public class RhinoDCmanagementControlFunction {
	private final static Logger LOG = Logger.getLogger(RhinoDCmanagementControlFunction.class);

	public static String getKey() {
		return LocaleControlConfig.getCurrentRecordKey();
	}

	public static void changeTaskStatus(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception{
		try {
			int status = Integer.MIN_VALUE;
			if (args != null && args.length > 0) {
				String statusStr = ObjectUtils.toString(args[0]);
				try {
					status = Byte.parseByte(statusStr);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			if (SystemInterface.MODE_EXECUTE.equals(((SystemInterface) ApplicationContext.getInstance()
					.getBean("systemInterface")).getMode())) {

//				if (status != Integer.MIN_VALUE) {
//					SubTaskManager manager = (SubTaskManager) ApplicationContext.getInstance()
//							.getBean("subTaskManager");
//
//					manager.changeTaskStatus(LocaleControlConfig.getCurrentRecordId(), LocaleControlConfig
//							.getCurrentRecordKey(), status);
//
//				}
			} else {
				LOG.info("failed = " + status);
			}
		}catch (Throwable e) {
				LOG.error("changeTaskStatus is error", e);
			}
		 finally {
			RhinoStandardFunction.exit(cx, thisObj, args, funObj);
		}
	}

}
