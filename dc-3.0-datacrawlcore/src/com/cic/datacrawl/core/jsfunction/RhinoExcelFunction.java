package com.cic.datacrawl.core.jsfunction;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObjectUtil;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

import com.cic.datacrawl.core.util.ExcelUtil;

/**
 * 支持Rhino所需的操作EXCEL文件的全局系统函数
 */
public final class RhinoExcelFunction {

	public static ExcelUtil createExcelFile(Context cx, Scriptable thisObj,
			Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			return null;
		}
		String filename = ObjectUtils.toString(args[0]);
		boolean overwrite = false;
		if (args.length > 1) {
			String overwriteString = ObjectUtils.toString(args[1]);
			overwrite = new Boolean(overwriteString).booleanValue();
		}
		try {
			return ExcelUtil.createNewExcel(filename, overwrite);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void addSheet(Context cx, Scriptable thisObj, Object[] args,
			Function funObj) {
		if (args.length < 3) {
			StringBuilder argsStr = new StringBuilder();
			for (int i = 0; i < args.length; ++i) {
				if (i > 0)
					argsStr.append(", ");
				argsStr.append("arg");
				argsStr.append("arg");
				argsStr.append(i + 1);
			}
			String errorMessage = "Invalid Function: addSheet("
					+ argsStr.toString() + ")";
			throw new RuntimeException(errorMessage);
		}
		Object firstArgument = NativeObjectUtil.jsObject2java(args[0]);
		ExcelUtil excel = (ExcelUtil) firstArgument;
		for (int i = 1; i < args.length; i += 2) {
			excel.addValue(ObjectUtils.toString(args[i]), NativeObjectUtil
					.jsObject2java(args[i + 1]));
		}

	}
}
