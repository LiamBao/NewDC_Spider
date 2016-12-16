package com.cic.datacrawl.core.initialize;

import com.cic.datacrawl.core.jsfunction.RhinoBrowserFunction;
import com.cic.datacrawl.core.jsfunction.RhinoConvertFunction;
import com.cic.datacrawl.core.jsfunction.RhinoEntityFunction;
import com.cic.datacrawl.core.jsfunction.RhinoExcelFunction;
import com.cic.datacrawl.core.jsfunction.RhinoFileFunction;
import com.cic.datacrawl.core.jsfunction.RhinoLoggerFunction;
import com.cic.datacrawl.core.jsfunction.RhinoStandardFunction;
import com.cic.datacrawl.core.rhino.RhinoContextGlobalRegister;

public class GlobalInitialization extends InitializeAble {

	@Override
	public void execute() {
		RhinoContextGlobalRegister.getInstance().registFunction(
																new String[] { "logDebug", "logError",
																		"logInfo", "logWarn",
																		"isDebugEnabled", "isInfoEnabled" },
																RhinoLoggerFunction.class);

		RhinoContextGlobalRegister.getInstance().registFunction(
																new String[] { "addDateFormat", "sendMail",
																		"getCurrentTime", "gc",
																		"getCurrentTimeMillis", "exit",
																		"getLocalHostname", "getLocalIp",
																		"getSystemProperty", "calcDays",
																		"inputVerifyCode", "isEditMode",
																		"load", "print", "println",
																		"setInterval", "getBean",
																		"setTimeout", "sleep", "isDate",
																		"finished", "failed", "nodeType" },
																RhinoStandardFunction.class);

		RhinoContextGlobalRegister.getInstance().registFunction(
																new String[] { "bin2hex", "bin2str",
																		"compress", "hex2bin", "html2text",
																		"formatDate", "parseTimestamp",
																		"parseToDouble", "parseToLong",
																		"makeRefindKey", "SHA", "str2bin",
																		"uncompress", "replaceAllInString",
																		"convertToNumber",
																		/*
																		 * "urlDecode",
																		 * "urlEncode"
																		 * ,
																		 */"urlRelative2Absolute",
																		"converToEntity", "toString",
																		"toXMLString", "toMD5" },
																RhinoConvertFunction.class);

		RhinoContextGlobalRegister.getInstance().registFunction(
																new String[] { "readFile", "writeFile",
																		"makeDir" }, RhinoFileFunction.class);

		RhinoContextGlobalRegister.getInstance()
				.registFunction(new String[] { "createExcelFile", "addSheet" }, RhinoExcelFunction.class);

		RhinoContextGlobalRegister.getInstance().registFunction(
																new String[] { "getCookies", "setCookies",
																		"showAllCookies" },
																RhinoBrowserFunction.class);

		RhinoContextGlobalRegister
				.getInstance()
				.registFunction(
								new String[] { "saveWithoutValidation", "commit", "clean", "setBufferedSize" },
								RhinoEntityFunction.class);
		// RhinoContextGlobalRegister.getInstance().registParameter("browser",
		// ApplicationContext.getInstance().getBean("browser"));
	}
}
