package com.cic.datacrawl.core.jsfunction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.NativeObjectUtil;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.ObjectUtils;
import org.mozilla.javascript.xmlimpl.RhinoXmlUtil;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.DefaultEntity;
import com.cic.datacrawl.core.util.DateUtil;
import com.cic.datacrawl.core.util.NumberUtil;
import com.cic.datacrawl.core.util.StringUtil;
import com.cic.datacrawl.core.util.XMLUtil;

/**
 * 支持Rhino所需的全局系统函数，主要做内容格式转换
 */
public final class RhinoConvertFunction {

	private final static Logger LOG = Logger.getLogger(RhinoConvertFunction.class);

	/**
	 * 获取html内容中的纯文本内容
	 * 
	 * @param xmlObject
	 *            相应html内容的E4X对象
	 * @return
	 */
	public static String urldecode(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in urldecode(str, charset)", "", 0);
			throw error;
		}

		String str = "";
		if (args.length >= 1) {
			str = ObjectUtils.toString(args[0]);
		}
		// URLEncoder encoder = URLEncoder.
		String enc = null;
		if (args.length >= 2) {
			enc = ObjectUtils.toString(args[1]);
		}
		try {
			if (enc == null || enc.trim().length() == 0) {
				return URLDecoder.decode(str, "utf-8");
			} else {
				return URLDecoder.decode(str, enc);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Invalid charset in urldecode(str, charset)", e);
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(URLEncoder.encode("&", "utf-8"));
		System.out.println(URLDecoder.decode("%2526", "utf-8"));
	}

	public static long convertToNumber(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException("Illegal Argument in convertToNumber(str)",
					"", 0);
			throw error;
		}
		return NumberUtil.convertToNumber(ObjectUtils.toString(args[0]));
	}

	/**
	 * 获取html内容中的纯文本内容
	 * 
	 * @param xmlObject
	 *            相应html内容的E4X对象
	 * @return
	 */
	public static String urlencode(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in urlencode(str, charset)", "", 0);
			throw error;
		}

		String str = "";
		if (args.length >= 1) {
			str = ObjectUtils.toString(args[0]);
		}
		// URLEncoder encoder = URLEncoder.
		String enc = null;
		if (args.length >= 2) {
			enc = ObjectUtils.toString(args[1]);
		}
		try {
			if (enc == null || enc.trim().length() == 0) {
				return URLEncoder.encode(str, "utf-8");
			} else {
				return URLEncoder.encode(str, enc);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Invalid charset in urlencode(str, charset)", e);
		}
	}

	/**
	 * 获取html内容中的纯文本内容
	 * 
	 * @param xmlObject
	 *            相应html内容的E4X对象
	 * @return
	 */
	public static String html2text(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in html2text(xmlobject, isRecursion)", "", 0);
			throw error;
		}
		boolean isRecursion = true;

		boolean hideNotShowNode = false;
		XMLObject xmlObject = null;
		if (args.length >= 1) {
			xmlObject = (XMLObject) args[0];
		}
		if (args.length >= 2) {
			try {
				isRecursion = new Boolean(ObjectUtils.toString(args[1])).booleanValue();
			} catch (Exception e) {
				isRecursion = true;
			}
		}
		if (args.length >= 3) {
			try {
				hideNotShowNode = new Boolean(ObjectUtils.toString(args[2])).booleanValue();
			} catch (Exception e) {
				hideNotShowNode = false;
			}
		}
		String ret = RhinoXmlUtil.toPureText(xmlObject, isRecursion, hideNotShowNode).trim();
		return ret;
	}

	public static String toString(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in toString(object) / toString(date, format)", "", 0);
			throw error;
		}
		String format = null;
		Object obj = NativeObjectUtil.jsObject2java(args[0]);
		if (args.length > 1) {
			format = ObjectUtils.toString(args[1]);
		}

		try {
			if (format != null && obj instanceof Date) {
				return DateUtil.format((Date) obj, format);
			}
			return ObjectUtils.toString(args[0]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String makeRefindKey(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in toMD5(arg1, arg2, arg3, arg4, arg5, arg6,........)", "", 0);
			throw error;
		}
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < args.length; ++i) {
			if (i > 0)
				sb.append(MD5_SPLIT);
			sb.append(ObjectUtils.toString(args[i]));
		}
		String str = sb.toString();
		if (LOG.isTraceEnabled())
			LOG.trace("Making MD5 String by " + str);

		return StringUtil.computeHexDigest(str.getBytes());
	}

	private static final String MD5_SPLIT = "_CICDATA_";

	public static String toMD5(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in toMD5(arg1, arg2, arg3, arg4, arg5, arg6,........)", "", 0);
			throw error;
		}
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < args.length; ++i) {
			if (i > 0)
				sb.append(MD5_SPLIT);
			sb.append(ObjectUtils.toString(args[i]));
		}
		String str = sb.toString();
		if (LOG.isTraceEnabled())
			LOG.trace("Making MD5 String by " + str);

		// return Stringu.toMD5(str);
		return StringUtil.MD5(str);
	}

	public static String replaceAllInString(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length < 3) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in replaceAllInString(str, regex, replacement)", "", 0);
			throw error;
		}
		String str = ObjectUtils.toString(args[0]);
		String regex = ObjectUtils.toString(args[1]);
		String replacement = ObjectUtils.toString(args[2]);

		return StringUtil.replaceAll(str, regex, replacement);
	}

	public static String formatDate(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in parseTimestamp(dateString, format, zonetime)", "", 0);
			throw error;
		}
		Date date = null;
		String format = null;
		SimpleTimeZone zone = null;
		if (args.length > 0) {
			Object dateObject = NativeObjectUtil.jsObject2java(args[0]);
			if (dateObject instanceof Date) {
				date = new Timestamp(((Date) dateObject).getTime());
			}
		}
		if (args.length > 1) {
			format = ObjectUtils.toString(args[1]);
		}
		if (args.length > 2) {
			int zoneTime = Integer.MIN_VALUE;
			try {
				zoneTime = Integer.parseInt(ObjectUtils.toString(args[2]));
			} catch (NumberFormatException e) {
				JavaScriptException error = new JavaScriptException(
						"Illegal Argument(zoneTime) in parseTimestamp(dateString, format, zonetime)", "", 0);
				throw error;
			}
			if (zoneTime >= -12 && zoneTime <= 12) {

				zone = new SimpleTimeZone(zoneTime * 3600 * 1000, "");
			} else {
				JavaScriptException error = new JavaScriptException(
						"Illegal Argument(zoneTime) in parseTimestamp(dateString, format, zonetime). (-12 <= zoneTime <= 12)",
						"", 0);

				throw error;
			}
		}
		SimpleDateFormat formater = new SimpleDateFormat(format == null ? DateUtil.DATETIMESTAMP_FORMAT
				: format);
		if (zone != null)
			formater.setTimeZone(zone);

		return formater.format(date);

	}

	public static Timestamp parseTimestamp(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in parseTimestamp(dateString, format, zonetime)", "", 0);
			throw error;
		}
		String dateString = null;
		String format = null;

		if (args.length > 0) {
			Object dateObject = NativeObjectUtil.jsObject2java(args[0]);
			if (dateObject instanceof Date) {
				return new Timestamp(((Date) dateObject).getTime());
			}
			dateString = ObjectUtils.toString(args[0]);
		}
		if (args.length > 1) {
			format = ObjectUtils.toString(args[1]);
		}
		int zoneTime = Integer.MIN_VALUE;
		if (args.length > 2) {
			try {
				zoneTime = Integer.parseInt(ObjectUtils.toString(args[2]));
			} catch (NumberFormatException e) {
			}
		}
		if (args.length > 2 && zoneTime != Integer.MIN_VALUE) {
			return DateUtil.format(dateString, format, zoneTime);
		} else if (args.length > 1) {
			return DateUtil.format(dateString, format);
		} else {
			return DateUtil.format(dateString);
		}
	}

	public static long parseToLong(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in parseToLong(numberString)", "", 0);
			throw error;
		}
		try {
			return new Double(ObjectUtils.toString(args[0])).longValue();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static double parseToDouble(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args == null || args.length == 0) {
			JavaScriptException error = new JavaScriptException(
					"Illegal Argument in parseToDouble(numberString)", "", 0);
			throw error;
		}
		try {
			return new Double(ObjectUtils.toString(args[0])).doubleValue();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 二进制压缩/解压
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] compress(NativeJavaArray bina) {
		byte[] input = (byte[]) bina.unwrap();

		ByteArrayOutputStream out = new ByteArrayOutputStream(input.length / 2);
		GZIPOutputStream gout = null;
		try {
			gout = new GZIPOutputStream(out);
			gout.write(input);

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (gout != null)
				try {
					gout.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
		}
		return out.toByteArray();
	}

	public static byte[] uncompress(NativeJavaArray bina) {
		byte[] input = (byte[]) bina.unwrap();

		ByteArrayInputStream in = new ByteArrayInputStream(input);
		ByteArrayOutputStream ostream = new ByteArrayOutputStream(input.length * 2);
		GZIPInputStream gin = null;
		try {
			gin = new GZIPInputStream(in, input.length);
			byte[] buf = new byte[1024];
			int readLen = gin.read(buf);
			while (readLen != -1) {
				ostream.write(buf, 0, readLen);
				readLen = gin.read(buf);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (gin != null)
				try {
					gin.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			if (ostream != null)
				try {
					ostream.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
		}
		return ostream.toByteArray();
	}

	// /**
	// * URL 编解码转换
	// *
	// * @param s
	// * @return
	// */
	// public static String urlEncode(String s, String code) {
	// try {
	// if ((code == null) || code.isEmpty())
	// code = "UTF-8";
	// return java.net.URLEncoder.encode(s, code);
	// } catch (UnsupportedEncodingException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// public static String urlDecode(String s, String code) {
	// try {
	// if ((code == null) || code.isEmpty())
	// code = "UTF-8";
	// return java.net.URLDecoder.decode(s, code);
	// } catch (UnsupportedEncodingException e) {
	// throw new RuntimeException(e);
	// }
	// }

	/**
	 * SHA-1 摘要处理
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] SHA(NativeJavaArray bina) {
		byte[] bin = (byte[]) bina.unwrap();
		try {
			MessageDigest digester = MessageDigest.getInstance("SHA");
			digester.update(bin);
			return digester.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 二进制与十六进制字符串的转换
	 * 
	 * @param bin
	 * @return
	 */
	public static String bin2hex(NativeJavaArray bina) {
		byte[] bin = (byte[]) bina.unwrap();
		char[] chars = new char[bin.length * 2];
		for (int i = 0; i < bin.length; i++) {
			int n = bin[i];
			if (n < 0)
				n += 256;
			chars[i * 2] = HEX_CHARS[n / 16];
			chars[i * 2 + 1] = HEX_CHARS[n % 16];
		}
		return new String(chars);
	}

	public final static char[] HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', };

	public static byte[] hex2bin(String hex) {
		char[] chars = hex.toCharArray();
		byte[] bin = new byte[chars.length / 2];
		for (int i = 0; i < bin.length; i++) {
			int n1 = (chars[i * 2] >= 'A') ? (chars[i * 2] - 'A' + 10) : (chars[i * 2] - '0');
			int n2 = (chars[i * 2 + 1] >= 'A') ? (chars[i * 2 + 1] - 'A' + 10) : (chars[i * 2 + 1] - '0');
			int n = n1 * 16 + n2;
			if (n > 128)
				n -= 256;
			bin[i] = (byte) n;
		}
		return bin;
	}

	/**
	 * 将相对路径的url转换为绝对路径
	 * 
	 * @param originUrl
	 * @param toUrl
	 * @return
	 */
	public static String urlRelative2Absolute(String url, String baseUrl) {
		try {
			return (new URL(new URL(baseUrl), url)).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 字符串与二进制数据的转换(UTF-8编码)
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] str2bin(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String bin2str(NativeJavaArray bina) {
		try {
			byte[] bin = (byte[]) bina.unwrap();
			return new String(bin, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static BaseEntity converToEntity(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (args.length == 0)
			return new DefaultEntity("");

		ArrayList<Object> list = new ArrayList<Object>();

		for (int i = 0; i < args.length; ++i) {
			Object obj = NativeObjectUtil.jsObject2java(args[i]);
			if (obj instanceof List) {
				@SuppressWarnings("unchecked")
				List<Object> theJsList = (List<Object>) obj;
				for (int j = 0; j < theJsList.size(); ++j) {
					list.add(theJsList.get(j));
				}
			} else {
				list.add(obj);
			}
		}
		return converToEntity(list);
	}

	private static BaseEntity converToEntity(ArrayList<Object> list) {
		BaseEntity ret = new DefaultEntity("");
		if (list.size() > 0) {
			if (list.size() == 1) {
				ret = converToEntity(list.get(0));
			} else {
				ArrayList<BaseEntity> entityList = new ArrayList<BaseEntity>();
				for (int i = 0; i < list.size(); ++i) {
					entityList.add(converToEntity(list.get(i)));
				}
				ret.set("MUTIL_ENTITY", entityList);
			}

		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private static BaseEntity converToEntity(Object object) {
		BaseEntity ret = new DefaultEntity("");
		if (object instanceof Map) {
			ret.setValueMap((Map<String, Object>) object);
		} else if (object instanceof List) {
			ret.set("MUTIL_ENTITY", object);
		} else if (object instanceof BaseEntity) {
			ret = (BaseEntity) object;
		} else {
			ret.set("VALUE", object);
		}
		return ret;
	}

	public static String toXMLString(Object o) {
		return XMLUtil.parseXMLValue(NativeObjectUtil.jsObject2java(o));
	}
}
