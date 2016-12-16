package com.cic.datacrawl.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.xmlimpl.ObjectUtils;

public class CodeUtil {
	public static String format(String sourceCode) {
		String formatScript = "var flags = {"
				+ "indent_size: 1,indent_char: '\\t',"
				+ "preserve_newlines: true,"
				+ "space_after_anon_function: true,"
				+ "keep_array_indentation: false,"
				+ "braces_on_own_line: false};"
				+ "js_beautify(sourceCode, flags);";

		URL beautifyJS = CodeUtil.class.getClassLoader().getResource(
				"JS_Beautify.js");

		Object jsReturn = null;
		String function = null;
		try {
			function = FileUtils.readFile(new File(beautifyJS.toURI()));
		} catch (IOException e) {
			return sourceCode;
		} catch (URISyntaxException e) {
			return sourceCode;
		}
		try {
			Context cx = Context.enter();

			ImporterTopLevel scope = new ImporterTopLevel(cx);
			scope.defineProperty("sourceCode", sourceCode, 0);

			cx.evaluateString(scope, function, "", 0, null);
			cx.evaluateString(scope, formatScript, "", 0, null);
			jsReturn = cx.evaluateString(scope, formatScript, "", 0, null);

		} finally {
			Context.exit();
		}
		return ObjectUtils.toString(jsReturn);
	}

}
