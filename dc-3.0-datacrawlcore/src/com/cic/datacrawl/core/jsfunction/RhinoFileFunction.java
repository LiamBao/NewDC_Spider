package com.cic.datacrawl.core.jsfunction;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.cic.datacrawl.core.util.FileUtils;

/**
 * 支持Rhino所需的为文件操作提供的全局系统函数，
 */
public final class RhinoFileFunction {

	private final static Logger LOG = Logger.getLogger(RhinoFileFunction.class);

	/**
	 * 创建文件夹
	 * 
	 * @param 文件夹绝对路径
	 * @return
	 */
	public static boolean makeDir(Context cx, Scriptable thisObj, Object[] args, Function funObj) {

		boolean ret = false;
		if (args.length == 1) {
			String folderName = org.mozilla.javascript.xmlimpl.ObjectUtils.toString(args[0]);

			File f = new File(folderName);
			if (!f.exists()) {
				ret = f.mkdirs();
			} else {
				if (f.isDirectory())
					ret = true;
			}

		} else {
			throw new RuntimeException("Invalid argument when calling makeDir(pathName)");
		}
		return ret;
	}

	/**
	 * Save content into a file as filename you set. default encode is UTF-8
	 * 
	 * @param filename
	 * @param content
	 */
	public static void writeFile(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args.length == 2 || args.length == 3) {
			String content = org.mozilla.javascript.xmlimpl.ObjectUtils.toString(args[1]);
			String filename = org.mozilla.javascript.xmlimpl.ObjectUtils.toString(args[0]);

			boolean overwrite = false;
			try {
				overwrite = new Boolean(org.mozilla.javascript.xmlimpl.ObjectUtils.toString(args[2]));
			} catch (Exception e) {
			}

			if (content == null || filename == null)
				return;

			try {
				FileUtils.saveFile(filename, content, overwrite);
			} catch (IOException e) {
				LOG.error(e.getMessage());
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException(
					"Invalid argument when calling writeFile(filename, contentString, overwrite)");
		}
	}

	/**
	 * 读取文件
	 * 
	 * @param filename
	 * @param encode
	 *            (encode can be empty, default encode is UTF-8).
	 * @return
	 */
	public static String readFile(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		String ret = "";

		if (args.length > 0) {
			String fileName = (String) args[0];

			File f = new File(fileName);
			if (!f.exists()) {
				String errorMessage = "File is not exist.(" + fileName + ")";
				LOG.error(errorMessage);
				throw new RuntimeException(errorMessage);
			}
			String encode = "utf-8";
			if (args.length > 1 && args[1] != null && ((String) args[1]).trim().length() > 0) {
				encode = (String) args[1];
			}
			if (LOG.isDebugEnabled())
				LOG.debug("Reading File: " + fileName);
			try {
				ret = FileUtils.readFile(fileName, encode);
			} catch (IOException e) {
				LOG.error(e.getMessage());
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
}
