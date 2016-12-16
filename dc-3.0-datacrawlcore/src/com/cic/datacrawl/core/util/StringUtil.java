package com.cic.datacrawl.core.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.ConvertUtils;

/**
 * @author Page 5
 */
public class StringUtil {
	private static final char[] numberChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ `~!@#$%^&*()_+-={}|[]\\;\':\",./<>?"
			.toCharArray();

	/**
	 * MD5 加密
	 * 
	 * @param s
	 *            - 需要加密的字符串
	 * @return
	 * @throws Exception
	 */
	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

		byte[] strTemp = s.getBytes();
		MessageDigest mdTemp = null;
		try {
			mdTemp = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Can not build md5 parser.", e);
		}
		mdTemp.update(strTemp);
		byte[] md = mdTemp.digest();
		int j = md.length;
		char str[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static long parseToLong(String string) {
		long ret = 0;

		char[] chars = string.toCharArray();
		int num = numberChars.length;
		for (int i = chars.length - 1; i >= 0; --i) {
			long power = 0;
			for (int j = chars.length - 1; j > i; --j) {
				power = power == 0 ? num : num * power;
			}
			int index = -1;
			for (int start = 0; start < num && index < 0; ++start) {
				if (chars[i] == numberChars[start]) {
					index = start;
				}
			}
			ret = ret + index * (power == 0 ? 1 : power);
		}
		return ret;
	}

	public static int calcRowNumber(String text, int startIndex) {
		if (text != null && text.length() > 0) {
			int lineNumber = 1;
			int start = 0;
			do {
				start = text.indexOf("\n", start);
				if (start >= 0 && start < startIndex) {
					++lineNumber;
					++start;
				} else {
					return lineNumber;
				}
			} while (start >= 0);

		}
		return 1;
	}

	public static String ToUpperFirstChar(String str) {
		if (str == null)
			return null;

		if (str.length() == 0) {
			return "";
		}
		if (str.length() == 1) {
			return str.toUpperCase();
		}
		String lowerStr = str.toLowerCase();
		return str.substring(0, 1).toUpperCase() + lowerStr.substring(1);

	}

	public static String computeDigest(byte[] data) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA").digest(data);
			StringBuffer sb = new StringBuffer(digest.length);
			for (int i = 0; i < digest.length; i++)
				sb.append((char) (digest[i] >= 0 ? digest[i] : digest[i] + 256));
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String computeHexDigest(byte[] data) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA").digest(data);
			StringBuffer sb = new StringBuffer(digest.length);
			for (int i = 0; i < digest.length; i++)
				sb.append(Integer.toString(digest[i] >= 0 ? digest[i] : digest[i] + 256, 16));
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 按分隔符拆分字符串。至少返回0长度数组。对每个中间结果不做trim。
	 * 
	 * @param source
	 * @param delim
	 * @return
	 */
	public static String[] fullSplit(String source, String delim) {
		return fullSplit(source, delim, false);
	}

	/**
	 * 按分隔符拆分字符串。至少返回empty列表。对每个中间结果不做trim。
	 * 
	 * @param source
	 * @param delim
	 * @return
	 */
	public static List<String> fullSplitToList(String source, String delim) {
		return fullSplitToList(source, delim, false);
	}

	/**
	 * 按分隔符拆分字符串。至少返回0长度数组。<tt>source</tt>为<tt>null或empty</tt> 时返回0长度数组。
	 * 
	 * @param source
	 * @param delim
	 * @param trim
	 *            对每个中间结果是否trim
	 * @return
	 */
	public static String[] fullSplit(String source, String delim, boolean trim) {
		if (source == null || source.length() == 0)
			return new String[0];

		List<String> lst = fullSplitToList(source, delim, trim);
		return (String[]) lst.toArray(new String[lst.size()]);
	}

	public static String buildRandomString(int length, char[] charArray) {

		StringBuilder builder = new StringBuilder();
		if (charArray == null || charArray.length == 0) {
			return null;
		}
		Random random = new Random((long) (System.currentTimeMillis() * Math.random()));
		for (int i = 0; i < length; ++i) {
			builder.append(charArray[(int) Math.abs((random.nextLong() % charArray.length))]);
		}

		return builder.toString();
	}

	public static String buildRandomString(int length) {
		return buildRandomString(length, UPPER_LOWER_AND_NUMBER_CHAR);
	}

	public static final char[] UPPER_ASCII_CHAR = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	public static final char[] LOWER_ASCII_CHAR = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public static final char[] NUMBER_ASCII_CHAR = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0' };

	public static final char[] UPPER_AND_LOWER_CHAR = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z' };

	public static final char[] UPPER_LOWER_AND_NUMBER_CHAR = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

	/**
	 * 按分隔符拆分字符串。至少返回0长度数组。对每个中间结果不做trim。
	 * 
	 * @param source
	 * @param delim
	 * @return
	 */
	public static String[] split(String source, String delim) {
		return split(source, delim, false);
	}

	/**
	 * 按分隔符拆分字符串。至少返回empty列表。对每个中间结果不做trim。
	 * 
	 * @param source
	 * @param delim
	 * @return
	 */
	public static List<String> splitToList(String source, String delim) {
		return splitToList(source, delim, false);
	}

	/**
	 * 按分隔符拆分字符串。至少返回0长度数组。<tt>source</tt>为<tt>null或empty</tt> 时返回0长度数组。
	 * 
	 * @param source
	 * @param delim
	 * @param trim
	 *            对每个中间结果是否trim
	 * @return
	 */
	public static String[] split(String source, String delim, boolean trim) {
		if (source == null || source.length() == 0)
			return new String[0];

		List<String> lst = splitToList(source, delim, trim);
		return (String[]) lst.toArray(new String[lst.size()]);
	}

	/**
	 * 统计在指定字符串中，待统计字符串的个数。
	 * 
	 * @param source
	 * @param subStr
	 *            待查找字符串
	 * @return
	 */
	public static int calcNum(String source, String subStr) {
		int ret = 0;
		if (source == null || subStr == null)
			return 0;
		for (int i = source.indexOf(subStr); i < source.length();) {
			if (i < 0)
				return ret;

			++ret;
			i = source.indexOf(subStr, i + subStr.length());
		}
		return ret;
	}

	public static List<String> splitToList(String source, String delim, boolean trim) {
		return splitToList(source, delim, trim, false);
	}

	public static List<String> fullSplitToList(String source, String delim, boolean trim) {
		return splitToList(source, delim, trim, true);
	}

	/**
	 * 按分隔符拆分字符串。至少返回empty列表。<tt>source</tt>为<tt>null或empty</tt> 时返回empty列表。
	 * 
	 * @param source
	 * @param delim
	 * @param trim
	 *            对每个中间结果是否trim
	 * @param fullSplit
	 *            true | false
	 *            当为true时，如果分隔符在字符首或字符尾，那么将在返回的List前后各加上一个空字符串，false时不加
	 * @return
	 */
	public static List<String> splitToList(String source, String delim, boolean trim, boolean fullSplit) {
		if (source == null || source.length() == 0)
			return new ArrayList<String>();
		List<String> lst = new ArrayList<String>(10);
		String[] s = source.split(delim);
		if (s != null) {
			for (int i = 0; i < s.length; ++i) {
				if (trim) {
					s[i] = s[i].trim();
				}
				lst.add(s[i]);
			}
		}
		if (fullSplit) {
			if (source.indexOf(delim) == 0) {
				lst.add(0, "");
			}
			if (source.lastIndexOf(delim) == 0) {
				lst.add("");
			}
		}
		return lst;
	}

	/**
	 * 拼接字符串：对<tt>lst</tt>的每个元素的toString形式，以<tt>delim</tt>为附加分隔符，依次 拼接起来。
	 * <p>
	 * 忽略<tt>lst</tt>中为<tt>null</tt>或toString为<tt>null</tt>的元素。<tt>lst</tt>如果 为
	 * <tt>null</tt>，返回<tt>null</tt>。
	 * 
	 * @param lst
	 * @param delim
	 *            可以为<tt>null</tt>
	 * @param prepend
	 *            是否最前面加上<tt>delim</tt>
	 * @return
	 */
	public static String concat(List lst, String delim, boolean prepend) {
		if (lst == null)
			return null;
		else if (lst.size() == 0)
			return prepend ? "" + delim : "";
		else if (lst.size() == 1) {
			Object ele = lst.get(0);
			String val = ele == null ? "" : ele.toString();
			val = val == null ? "" : val;
			return prepend ? "" + delim + val : "" + val;
		}

		StringBuffer sb = new StringBuffer(prepend ? "" + delim : "");
		boolean heading = true;
		for (int i = 0; i < lst.size(); i++) {
			Object ele = lst.get(i);
			if (ele == null)
				continue;
			String val = ele.toString();
			if (val == null)
				continue;

			sb.append(heading ? val : delim + val);
			heading = false;
		}

		return sb.toString();
	}

	/**
	 * 判断数组中是否存在（Object.equals）指定对象。如果<tt>arr</tt>为 <tt>null</tt>或长度为<tt>0</tt>
	 * ，返回<tt>false</tt>。
	 * 
	 * @param arr
	 * @param element
	 * @return
	 */
	public static boolean contains(Object[] arr, Object element) {
		if (arr == null || arr.length == 0)
			return false;

		if (element == null)
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == null)
					return true;
			}
		else
			for (int i = 0; i < arr.length; i++) {
				if (element.equals(arr[i]))
					return true;
			}

		return false;
	}

	public static String replaceAll(String source, String regex, String replacement) {
		return replaceAll(new StringBuffer(source), regex, replacement).toString();
	}

	public static StringBuffer replaceAll(StringBuffer buffer, String regex, String replacement) {
		if (regex == null || regex.length() == 0) {
			return buffer;
		}
		int place;
		int from = 0;
		while ((place = buffer.toString().indexOf(regex, from)) >= 0) {
			buffer.replace(place, place + regex.length(), replacement);
			from = place + replacement.length();
		}
		return buffer;
	}

	/**
	 * 判断一个字符串的值是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str.trim())) {
			return false;
		}
		return true;
	}

	public static String col2Attr(String col) {
		col = col.toLowerCase();
		StringBuffer attr = new StringBuffer();
		int i = 0, j = 0;
		while ((i = col.indexOf('_', j)) != -1) {
			attr.append(col.substring(j, i));
			attr.append(col.substring(i + 1, i + 2).toUpperCase());
			j = i + 2;
		}
		attr.append(col.substring(j));
		return attr.toString();
	}

	public static String removeSuffix(String str, String suffix) {
		if (!str.endsWith(suffix))
			return str;
		else {
			int i = str.lastIndexOf(suffix);
			return str.substring(0, i);
		}
	}

	public static String toUtf8(String str) {
		return encode(str, "8859_1", "UTF-8");
	}

	public static String fromDefaultToUTF8(String str) {
		try {
			return new String(str.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String readUTF8(String str) {
		try {
			return new String(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String encode(String str, String formCodeType, String toCodeType) {
		try {
			return new String(str.getBytes(formCodeType), toCodeType);
		} catch (UnsupportedEncodingException e) {
		}
		return "";
	}

	/**
	 * nengzhu 把字符串数组变成需要的List集合
	 * 
	 * @param params
	 * @param flag
	 *            1--Long 2--Integer 3--Double
	 * @return
	 */
	public static ArrayList change(String[] params, int flag) {
		ArrayList arrayList = new ArrayList();
		for (int i = 0; i < params.length; i++) {
			switch (flag) {
			case 1:
				arrayList.add(new Long(params[i]));
				break;
			case 2:
				arrayList.add(new Integer(params[i]));
				break;
			case 3:
				arrayList.add(new Double(params[i]));
				break;
			default:
				break;
			}
		}
		return arrayList;
	}

	/**
	 * 一个转化String数组到各种类型的基本数据类型的公用方法 nengzhu 使用示范： int ids [] = (int
	 * [])StringUtil.parseString(strs, int.class);
	 */
	public static Object parseString(String[] params, Class type) {
		ConvertUtils cub = new ConvertUtils();
		if (params != null)
			return cub.convert(params, type);
		else
			return null;
	}

	public static boolean isNull(String str) {
		boolean ret = false;
		if (str == null)
			return true;
		if ("null".equalsIgnoreCase(str))
			return true;
		if ("n/a".equalsIgnoreCase(str))
			return true;
		return ret;
	}

	/**
	 * 将Hashtable 中的key替换为value，其中key是单个字符
	 * 
	 * @param strSource
	 * @param hs
	 * @return
	 * @throws ActionException
	 */
	public static String mutiReplace(String strSource, java.util.Hashtable hs) {
		strSource = strSource == null ? "" : strSource;
		String strReturn = "";
		int i = 0;

		while (i < strSource.length()) {
			String strTemp = (String) hs.get("" + strSource.charAt(i));
			// 如果hs表中无此值，原字符返回，否则，返回替换后字符串
			if (strTemp == null) {
				strReturn += strSource.charAt(i);
			} else {
				strReturn += strTemp;
			}
			i++;
		}

		return strReturn;
	}

	/**
	 * 将字符串中的html代码替换成html表示代码<br>
	 * 比如"\""替换为&quot; 其中&符号需要第一个替换 如果有替他需要替换的字符，可以进一步完善hashtable
	 * 
	 * 注：可替换的前提是这些符号必须能够正常的被处理
	 * 
	 * @param str
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public static String codeReplace(String strSource) {
		// 返回字符串
		String strReturn = "";

		java.util.Hashtable hs = new java.util.Hashtable();
		hs.put("<", "&lt;");
		// hs.put(">", "&gt;");
		// hs.put("\"", "&quot;");

		// 替换hs表中其他符号
		strReturn = mutiReplace(strSource, hs);

		return strReturn;
	}

	public static String buildJSRegex(String str) {
		if (str == null || str.trim().length() == 0) {
			return null;
		}
		str = replaceAll(str, "\\", "\\\\");
		str = replaceAll(str, ".", "\\.");
		str = replaceAll(str, "/", "\\/");
		str = replaceAll(str, "?", "\\?");
		str = replaceAll(str, "+", "\\+)");
		str = replaceAll(str, "*", "\\*");
		str = replaceAll(str, "{", "\\{");
		str = replaceAll(str, "}", "\\}");
		str = replaceAll(str, "[", "\\[");
		str = replaceAll(str, "]", "\\]");
		str = replaceAll(str, "(", "\\(");
		str = replaceAll(str, ")", "\\)");
		str = replaceAll(str, "^", "\\^)");
		str = replaceAll(str, "$", "\\$)");
		str = replaceAll(str, "|", "\\|");
		str = replaceAll(str, "-", "\\-");

		StringBuilder sb = new StringBuilder(str);
		int start = -1;
		int end = -1;
		boolean isStartSetted = false;
		for (int i = 0; i < sb.length(); ++i) {
			char c = sb.charAt(i);
			if ('0' <= c && '9' >= c) {
				if (isStartSetted) {
					end = i + 1;
				} else {
					start = i;
					isStartSetted = true;
				}
			} else {
				if (start >= 0) {
					if (end < start) {
						end = start + 1;
					}
					if (end < sb.length()) {
						sb.replace(start, end, "\\d+");
					} else {
						sb.delete(start, end);
						sb.append("\\d+");
					}
				}
				start = end = -1;
				isStartSetted = false;
			}
		}
		if (start >= 0) {
			if (end < start || end >= sb.length()) {
				for (int i = sb.length() - 1; i >= start; --i)
					sb.deleteCharAt(i);
			} else {
				sb.delete(start, end);
			}
			sb.append("\\d+");
		}
		str = sb.toString();
		return str;
	}

	public static String convertDoubleToStringWithoutScientificNotaion(double d) {
		return NO_SCIENTIFIC_DOUBLE_FORMATTER.format(d);
	}

	public static boolean containsIgnoreCase(List strings, String string) {
		for (Iterator i = strings.iterator(); i.hasNext();) {
			String s = (String) i.next();
			if (s.equalsIgnoreCase(string))
				return true;
		}

		return false;
	}

	public static boolean containsDigits(String s) {
		for (int i = 0; i < s.length(); i++)
			if (Character.isDigit(s.charAt(i)))
				return true;

		return false;
	}

	public static List toList(String str, boolean trim) {
		return toList(str, ',', trim);
	}

	public static List toList(String str, char separator, boolean trim) {
		return toList(str, new String(new char[] { separator }), trim);
	}

	public static List toList(String str, String separators, boolean trim) {
		List tokens = new ArrayList();
		StringTokenizer st = new StringTokenizer(str, separators);
		do {
			if (!st.hasMoreTokens())
				break;
			String token = st.nextToken();
			if (trim) {
				token = token.trim();
				if (token.equals(""))
					continue;
			}
			tokens.add(token);
		} while (true);
		return tokens;
	}

	public static String toString(List objects, char separator) {
		return toString(objects, String.valueOf(separator));
	}

	public static String toString(List objects, String separator) {
		if (objects.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		Iterator i = objects.iterator();
		if (i.hasNext())
			sb.append(i.next());
		for (; i.hasNext(); sb.append(separator).append(i.next()))
			;
		return sb.toString();
	}

	public static String capitalize(String s) {
		boolean isDifferent = false;
		int len = s.length();
		char chars[] = new char[len];
		int pos = 0;
		boolean isWord = false;
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			int codePoint;
			if (Character.isHighSurrogate(c) && i + 1 < len)
				codePoint = Character.toCodePoint(c, s.charAt(i));
			else
				codePoint = c;
			int newCodePoint = codePoint;
			if (Character.isLetter(codePoint)) {
				if (isWord)
					newCodePoint = Character.toLowerCase(codePoint);
				else
					newCodePoint = Character.toUpperCase(codePoint);
				isWord = true;
			} else {
				isWord = false;
			}
			isDifferent |= codePoint != newCodePoint;
			int charSize = Character.isSupplementaryCodePoint(newCodePoint) ? 2 : 1;
			if (pos + charSize > chars.length) {
				char tmp[] = new char[pos + charSize];
				System.arraycopy(chars, 0, tmp, 0, chars.length);
				chars = tmp;
			}
			pos += Character.toChars(newCodePoint, chars, pos);
		}

		return isDifferent ? new String(chars, 0, pos) : s;
	}

	/**
	 * @deprecated Method capitalizeString is deprecated
	 */

	public static String capitalizeString(String str) {
		StringTokenizer st = new StringTokenizer(str);
		StringBuffer res = new StringBuffer(128);
		do {
			if (!st.hasMoreTokens())
				break;
			String s = st.nextToken();
			int len = s.length();
			if (len == 3 && s.charAt(1) < 'A')
				res.append(s);
			else if (len > 2) {
				StringTokenizer st2 = new StringTokenizer(s, "-");
				if (st2.hasMoreTokens())
					s = st2.nextToken();
				res.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase());
				for (; st2.hasMoreTokens(); res.append(s.substring(0, 1).toUpperCase())
						.append(s.substring(1).toLowerCase())) {
					s = st2.nextToken();
					res.append("-");
				}

			} else {
				res.append(s);
			}
			if (st.hasMoreTokens())
				res.append(" ");
		} while (true);
		return res.toString();
	}

	/**
	 * @deprecated Method capitalizeDefault is deprecated
	 */

	public static String capitalizeDefault(String inputString) {
		return capitalize(inputString, new String[] { "and", "as", "for", "in", "of", "the", "this", "to" },
							true, 1, null);
	}

	public static String capitalize(String inputString, String wordsThatShouldNotBeChanged[],
			boolean ignoreCase, int minNumberOfCharsInWordThatShouldBeCapitalized, String extraDelimiters) {
		List listOfWordsThatShouldNotBeCapitilized = ((List) (wordsThatShouldNotBeChanged != null ? Arrays
				.asList(wordsThatShouldNotBeChanged) : ((List) (new ArrayList()))));
		if (ignoreCase) {
			for (int i = 0; i < listOfWordsThatShouldNotBeCapitilized.size(); i++) {
				String str = (String) listOfWordsThatShouldNotBeCapitilized.get(i);
				listOfWordsThatShouldNotBeCapitilized.set(i, str.toLowerCase());
			}

		}
		String result = "";
		String delim = (new StringBuilder()).append(extraDelimiters != null ? extraDelimiters : "")
				.append("\t\n\r\f ").toString();
		for (StringTokenizer stringTokenizer = new StringTokenizer(inputString, delim, true); stringTokenizer
				.hasMoreTokens();) {
			String token = stringTokenizer.nextToken();
			if (token.length() == 1 && delim.indexOf(token) != -1)
				result = (new StringBuilder()).append(result).append(token).toString();
			else
				result = (new StringBuilder()).append(result)
						.append(
								token.length() < minNumberOfCharsInWordThatShouldBeCapitalized
										|| listOfWordsThatShouldNotBeCapitilized
												.contains(ignoreCase ? ((Object) (token.toLowerCase()))
														: ((Object) (token))) ? token
										: capitalizeFirstChar(token.toLowerCase())).toString();
		}

		return result;
	}

	public static String capitalizeFirstChar(String str) {
		switch (str.length()) {
		case 0: // '\0'
			return str;

		case 1: // '\001'
			return str.toUpperCase();
		}
		return (new StringBuilder()).append(str.substring(0, 1).toUpperCase()).append(str.substring(1))
				.toString();
	}

	public static String capitalizeFirstCharLowerCaseRest(String str) {
		return str.length() <= 0 ? str : (new StringBuilder()).append(str.substring(0, 1).toUpperCase())
				.append(str.substring(1).toLowerCase()).toString();
	}

	public static String insertSpacesAtCamelHumps(String s) {
		StringBuffer sb = new StringBuffer();
		char prevChar = '\0';
		for (int i = 0; i < s.length(); i++) {
			char curChar = s.charAt(i);
			if (i > 0
				&& (Character.isLetter(prevChar)
					&& Character.isLowerCase(prevChar)
					&& Character.isLetter(curChar)
					&& Character.isUpperCase(curChar)
					|| Character.isLetter(prevChar)
					&& Character.isDigit(curChar) || Character.isDigit(prevChar)
														&& Character.isLetter(curChar)))
				sb.append(' ');
			sb.append(curChar);
			prevChar = curChar;
		}

		return sb.toString();
	}

	public static String trim(String string) {
		return trim(string, "\240");
	}

	public static String trim(String src, String chars) {
		int begin;
		for (begin = 0; begin < src.length()
						&& (Character.isWhitespace(src.charAt(begin)) || chars.indexOf(src.charAt(begin)) != -1); begin++)
			;
		if (begin == src.length())
			return "";
		int end;
		for (end = src.length() - 1; end > -1
										&& (Character.isWhitespace(src.charAt(end)) || chars.indexOf(src
												.charAt(end)) != -1); end--)
			;
		return src.substring(begin, end + 1);
	}

	public static List trim(List strings) {
		List trimmedStrings = new ArrayList(strings.size());
		String s;
		for (Iterator i = strings.iterator(); i.hasNext(); trimmedStrings.add(s.trim()))
			s = (String) i.next();

		return trimmedStrings;
	}

	public static boolean startsWith(String string, String prefix, boolean ignoreCase) {
		if (string.length() < prefix.length()) {
			return false;
		} else {
			String actualPrefix = string.substring(0, prefix.length());
			return ignoreCase ? actualPrefix.equalsIgnoreCase(prefix) : actualPrefix.equals(prefix);
		}
	}

	public static boolean endsWith(String string, String suffix, boolean ignoreCase) {
		String caseConvertedString = ignoreCase ? string.toLowerCase() : string;
		String caseConvertedSuffix = ignoreCase ? suffix.toLowerCase() : suffix;
		return caseConvertedString.endsWith(caseConvertedSuffix);
	}

	public static String removeSuffix(String string, String suffix, boolean ignoreCase) {
		return endsWith(string, suffix, ignoreCase) ? string.substring(0, string.length() - suffix.length())
				: string;
	}

	public static String removeSuffixes(String string, List suffixes, boolean ignoreCase) {
		for (Iterator i = suffixes.iterator(); i.hasNext();) {
			String suffix = (String) i.next();
			if (endsWith(string, suffix, ignoreCase))
				return removeSuffix(string, suffix, ignoreCase);
		}

		return string;
	}

	public static String removeSuffixStartingFrom(String string, String delimiter) {
		int index = string.indexOf(delimiter);
		return index == -1 ? string : string.substring(0, index);
	}

	public static List toLines(String s) {
		List lines = new ArrayList();
		int i = 0;
		do {
			if (i >= s.length())
				break;
			int lineEnd = s.indexOf('\n', i);
			if (lineEnd == -1) {
				lines.add(s.substring(i));
				break;
			}
			lines.add(s.substring(i, lineEnd));
			i = lineEnd + 1;
		} while (true);
		return lines;
	}

	public static String toCSV(String stringArray[]) {
		String strResult = "";
		if (stringArray != null && stringArray.length > 0) {
			StringBuffer result = new StringBuffer(stringArray.length * 2);
			for (int i = 0; i < stringArray.length; i++) {
				String s = stringArray[i];
				if (s.indexOf(",") != -1 || s.indexOf(" ") != -1) {
					if (s.indexOf("\\") != -1)
						s = searchAndReplace(s, "\\", "\\\\");
					if (s.indexOf("\"") != -1)
						s = searchAndReplace(s, "\"", "\\\"");
					result.append('"').append(s).append('"');
				} else {
					result.append(s);
				}
				if (i != stringArray.length - 1)
					result.append(",");
			}

			strResult = result.toString();
		}
		return strResult;
	}

	public static String[] fromCSV(String csv) {
		ArrayList strArr = new ArrayList(csv.length() / 6);
		StringBuffer buf = new StringBuffer();
		boolean inquotes = false;
		boolean behindEscapeChar = false;
		int firstQuote = 0x7fffffff;
		int lastQuote = 0;
		for (int i = 0; i < csv.length(); i++) {
			if (behindEscapeChar) {
				buf.append(csv.charAt(i));
				behindEscapeChar = false;
				continue;
			}
			if (csv.charAt(i) == ',' && !inquotes) {
				for (int j = buf.length() - 1; j >= lastQuote && buf.charAt(j) == ' '; j--)
					buf.deleteCharAt(j);

				for (int j = 0; j < firstQuote && buf.length() > 0 && buf.charAt(0) == ' '; j++)
					buf.deleteCharAt(0);

				strArr.add(buf.toString());
				buf.setLength(0);
				firstQuote = 0x7fffffff;
				lastQuote = 0;
				continue;
			}
			if (csv.charAt(i) == '"') {
				inquotes = !inquotes;
				if (firstQuote == 0x7fffffff)
					firstQuote = buf.length();
				lastQuote = buf.length();
				continue;
			}
			if (csv.charAt(i) == '\\')
				behindEscapeChar = true;
			else
				buf.append(csv.charAt(i));
		}

		for (int j = buf.length() - 1; j >= lastQuote && buf.charAt(j) == ' '; j--)
			buf.deleteCharAt(j);

		for (int j = 0; j < firstQuote && buf.length() > 0 && buf.charAt(0) == ' '; j++)
			buf.deleteCharAt(0);

		strArr.add(buf.toString());
		return (String[]) (String[]) strArr.toArray(new String[0]);
	}

	public static Map toStringMap(String s, boolean allowEmptyKey) {
		Map map = new HashMap();
		int lineCount = 0;
		for (Iterator i = toLines(s).iterator(); i.hasNext(); lineCount++) {
			String line = (String) i.next();
			line = line.trim();
			if (line.length() <= 0 || line.startsWith("//"))
				continue;
			int equalSignIndex = getFirstUnquotedOccurrence(line, '=');
			if (equalSignIndex == -1)
				throw new IllegalArgumentException((new StringBuilder())
						.append("The '=' character is missing on line ").append(lineCount + 1).append(".")
						.toString());
			String keyString;
			String valueString;
			try {
				keyString = line.substring(0, equalSignIndex).trim();
				if (isQuotedString(keyString))
					keyString = decodeQuotedBackslashEncodedString(keyString);
				else if (keyString.length() == 0
							|| keyString.indexOf('"') != -1
							|| keyString.indexOf('=') != -1)
					throw new IllegalArgumentException("Invalid text on the left side of the '=' character.");
				if (!allowEmptyKey && keyString.length() == 0)
					throw new IllegalArgumentException(
							"The text on the left side of the '=' character cannot be empty.");
				valueString = line.substring(equalSignIndex + 1).trim();
				if (isQuotedString(valueString))
					valueString = decodeQuotedBackslashEncodedString(valueString);
				else if (valueString.length() == 0
							|| valueString.indexOf('"') != -1
							|| valueString.indexOf('=') != -1)
					throw new IllegalArgumentException("Invalid text on the right side of the '=' character.");
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException((new StringBuilder()).append("Error on line ")
						.append(lineCount + 1).append(": ").append(e.getMessage()).toString());
			}
			if (map.containsKey(keyString))
				throw new IllegalArgumentException((new StringBuilder()).append("Error on line ")
						.append(lineCount + 1).append(": A value for \"").append(keyString)
						.append("\" has already been defined.").toString());
			map.put(keyString, valueString);
		}

		return map;
	}

	public static boolean isQuotedString(String s) {
		return s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"';
	}

	public static String decodeQuotedBackslashEncodedString(String s) {
		return decodeQuotedBackslashEncodedString(s, false);
	}

	public static String decodeQuotedBackslashEncodedString(String s, boolean allowEscapeForAnyCharacter) {
		if (!isQuotedString(s)) {
			throw new IllegalArgumentException("Missing quotes.");
		} else {
			s = removeQuotes(s);
			return decodeBackslashEncodedString(s, allowEscapeForAnyCharacter);
		}
	}

	public static String decodeBackslashEncodedString(String s) {
		return decodeBackslashEncodedString(s, false);
	}

	public static String decodeBackslashEncodedString(String s, boolean allowEscapeForAnyCharacter) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\\') {
				if (i + 1 == s.length())
					throw new IllegalArgumentException("Invalid escape character: \\.");
				i++;
				c = s.charAt(i);
				switch (c) {
				case 110: // 'n'
					sb.append('\n');
					continue;

				case 114: // 'r'
					sb.append('\r');
					continue;

				case 102: // 'f'
					sb.append('\f');
					continue;

				case 116: // 't'
					sb.append('\t');
					continue;

				case 98: // 'b'
					sb.append('\b');
					continue;

				case 34: // '"'
					sb.append('"');
					continue;

				case 39: // '\''
					sb.append('\'');
					continue;

				case 92: // '\\'
					sb.append('\\');
					continue;

				case 117: // 'u'
					if (i + 4 >= s.length())
						throw new IllegalArgumentException((new StringBuilder())
								.append("Invalid escape sequence: \\u").append(s.substring(i + 1))
								.append(".").toString());
					String hexEncoding = s.substring(i + 1, i + 5);
					i += 4;
					try {
						sb.append((char) Integer.parseInt(hexEncoding, 16));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException((new StringBuilder())
								.append("Invalid escape sequence: \\u").append(hexEncoding).append(".")
								.toString());
					}
					continue;
				}
				if (allowEscapeForAnyCharacter)
					sb.append(c);
				else
					throw new IllegalArgumentException((new StringBuilder())
							.append("Invalid escape sequence: \\").append(c).append(".").toString());
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	public static String quote(String s) {
		StringBuilder sb = new StringBuilder(s.length() + 2);
		sb.append('"');
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case 10: // '\n'
				sb.append("\\n");
				break;

			case 13: // '\r'
				sb.append("\\r");
				break;

			case 12: // '\f'
				sb.append("\\f");
				break;

			case 8: // '\b'
				sb.append("\\b");
				break;

			case 34: // '"'
				sb.append("\\\"");
				break;

			case 39: // '\''
				sb.append("\\'");
				break;

			case 92: // '\\'
				sb.append("\\\\");
				break;

			default:
				if (c < ' ') {
					sb.append("\\u00");
					sb.append(Character.toUpperCase(Character.forDigit((c & 0xf0) >> 4, 16)));
					sb.append(Character.toUpperCase(Character.forDigit(c & 0xf, 16)));
				} else {
					sb.append(c);
				}
				break;
			}
		}

		sb.append('"');
		return sb.toString();
	}

	public static int getFirstUnquotedOccurrence(String s, char c) {
		boolean inQuotes = false;
		for (int i = 0; i < s.length(); i++) {
			char curChar = s.charAt(i);
			if (curChar == '"') {
				inQuotes = !inQuotes;
				continue;
			}
			if (curChar == '\\' && inQuotes) {
				if (i + 1 >= s.length())
					continue;
				i++;
				if (s.charAt(i) == 'u')
					i = Math.min(i + 4, s.length() - 1);
				continue;
			}
			if (!inQuotes && curChar == c)
				return i;
		}

		return -1;
	}

	public static String removeQuotes(String s) {
		if ((s.startsWith("\"") && s.endsWith("\"") || s.startsWith("'") && s.endsWith("'"))
			&& s.length() >= 2)
			return s.substring(1, s.length() - 1);
		else
			return s;
	}

	/**
	 * @deprecated Method removeSpaces is deprecated
	 */

	public static String removeSpaces(String string) {
		int length = string.length();
		StringBuffer result = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			char next = string.charAt(i);
			if (next > ' ')
				result.append(next);
		}

		return result.toString();
	}

	public static String removeWhiteSpaces(String string) {
		return removeWhiteSpaces(string, false);
	}

	public static String removeWhiteSpaces(String string, boolean removeNbsp) {
		int length = string.length();
		StringBuffer result = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			char next = string.charAt(i);
			if (!Character.isWhitespace(next) && (!removeNbsp || next != '\240'))
				result.append(next);
		}

		return result.toString();
	}

	/**
	 * @deprecated Method collapseSpaces is deprecated
	 */

	public static String collapseSpaces(String string, boolean trim) {
		int length = string.length();
		StringBuffer result = new StringBuffer(length);
		boolean inSpaces = false;
		for (int i = 0; i < length; i++) {
			char next = string.charAt(i);
			if (next > ' ') {
				result.append(next);
				inSpaces = false;
				continue;
			}
			if (!inSpaces) {
				result.append(' ');
				inSpaces = true;
			}
		}

		return trim ? result.toString().trim() : result.toString();
	}

	/**
	 * @deprecated Method collapseSpaces is deprecated
	 */

	public static String collapseSpaces(String string) {
		return collapseSpaces(string, true);
	}

	public static String collapseWhiteSpaces(String string, boolean collapseNbsp) {
		int length = string.length();
		StringBuffer result = new StringBuffer(length);
		boolean inSpaces = false;
		for (int i = 0; i < length; i++) {
			char next = string.charAt(i);
			if (!Character.isWhitespace(next) && (!collapseNbsp || next != '\240')) {
				result.append(next);
				inSpaces = false;
				continue;
			}
			if (!inSpaces) {
				result.append(' ');
				inSpaces = true;
			}
		}

		return result.toString();
	}

	public static String trimStart(String s) {
		int length = s.length();
		int i;
		for (i = 0; i < length && Character.isWhitespace(s.charAt(i)); i++)
			;
		return s.substring(i);
	}

	public static String trimEnd(String s) {
		int i;
		for (i = s.length() - 1; i > -1 && Character.isWhitespace(s.charAt(i)); i--)
			;
		return s.substring(0, i + 1);
	}

	public static String convertNBSPToSpace(String s) {
		return s.replace('\240', ' ');
	}

	public static String lineWrapText(String text, int desiredLineLength, int maxLineLength) {
		if (maxLineLength != -1 && maxLineLength < desiredLineLength)
			throw new IllegalArgumentException("Max. line length cannot be smaller than desired line length.");
		StringBuffer wrappedText = new StringBuffer();
		boolean firstLine = true;
		boolean newlinePending = false;
		for (int i = 0; i < text.length();) {
			for (; i < text.length() && Character.isWhitespace(text.charAt(i)) && text.charAt(i) != '\n'; i++)
				;
			if (i == text.length())
				break;
			if (!firstLine) {
				wrappedText.append("\n");
				newlinePending = false;
			}
			int wordWrapEnd = Math.min(text.length(), i + desiredLineLength);
			if (text.indexOf("\n", i) != -1)
				wordWrapEnd = Math.min(wordWrapEnd, text.indexOf("\n", i));
			for (; wordWrapEnd < text.length() && !Character.isWhitespace(text.charAt(wordWrapEnd)); wordWrapEnd++)
				;
			if (maxLineLength == -1 || wordWrapEnd - i <= maxLineLength) {
				wrappedText.append(text.substring(i, wordWrapEnd));
				i = wordWrapEnd;
				if (i < text.length() && text.charAt(i) == '\n') {
					i++;
					newlinePending = true;
				}
			} else {
				wrappedText.append(text.substring(i, i + maxLineLength));
				wrappedText.append('-');
				i += maxLineLength;
			}
			firstLine = false;
		}

		if (newlinePending)
			wrappedText.append('\n');
		return wrappedText.toString();
	}

	public static String lineWrap(String text, int maxLineLength) {
		StringBuffer buf = new StringBuffer(text.length());
		String line;
		for (StringTokenizer token = new StringTokenizer(text, "\n", true); token.hasMoreTokens(); buf
				.append(line))
			for (line = token.nextToken(); line.length() > maxLineLength; line = line
					.substring(maxLineLength, line.length()))
				buf.append((new StringBuilder()).append(line.substring(0, maxLineLength)).append("\n")
						.toString());

		return buf.toString();
	}

	public static String truncate(String s, int length) {
		if (s.length() > length)
			s = (new StringBuilder()).append(trimEnd(s.substring(0, length))).append("...").toString();
		return s;
	}

	public static String truncateLines(String s, int lines) {
		int i = 0;
		int lineCount = 0;
		do {
			if (lineCount >= lines)
				break;
			int lineEnd = s.indexOf('\n', i);
			if (lineEnd == -1) {
				i = s.length();
				break;
			}
			lineCount++;
			i = lineEnd + 1;
		} while (true);
		if (i < s.length())
			return (new StringBuilder()).append(s.substring(0, i)).append("...").toString();
		else
			return s;
	}

	public static String truncateToMaxLength(String s, int length) {
		if (length < 3)
			throw new IllegalArgumentException("length must be 3 or greater.");
		if (s.length() > length) {
			s = trimEnd(s);
			if (s.length() > length)
				s = (new StringBuilder()).append(s.substring(0, length - 3)).append("...").toString();
		}
		return s;
	}

	public static String indent(String string, int indentSize) {
		char indentChars[] = new char[indentSize];
		Arrays.fill(indentChars, ' ');
		String indent = new String(indentChars);
		StringBuffer sb = new StringBuffer(string.length());
		String line;
		for (StringTokenizer tokens = new StringTokenizer(string, "\n", true); tokens.hasMoreTokens(); sb
				.append(line)) {
			line = tokens.nextToken();
			if (line.trim().length() > 0)
				sb.append(indent);
		}

		return sb.toString();
	}

	public static String unindent(String string) {
		int remove = -1;
		StringTokenizer tokens = new StringTokenizer(string, "\n");
		do {
			if (!tokens.hasMoreTokens())
				break;
			String line = tokens.nextToken();
			if (line.trim().length() > 0) {
				int i;
				for (i = 0; i < line.length() && Character.isWhitespace(line.charAt(i)); i++)
					;
				if (remove == -1 || i < remove)
					remove = i;
			}
		} while (true);
		if (remove <= 0)
			return string;
		StringBuffer sb = new StringBuffer(string.length());
		String line;
		for (tokens = new StringTokenizer(string, "\n", true); tokens.hasMoreTokens(); sb.append(line)) {
			line = tokens.nextToken();
			if (line.length() > remove)
				line = line.substring(remove);
		}

		return sb.toString();
	}

	public static String convertToJavaLineBreaks(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\r') {
				if (i + 1 < s.length() && s.charAt(i + 1) == '\n')
					i++;
				sb.append('\n');
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	public static int indexOfIgnoreCase(String source, String stringToFind) {
		return source.toLowerCase().indexOf(stringToFind.toLowerCase());
	}

	public static String convertToCustomLineBreaks(String s, String lineSeparator) {
		return searchAndReplace(s, "\n", lineSeparator);
	}

	public static String convertToPlatformLineBreaks(String s) {
		return searchAndReplace(s, "\n", System.getProperty("line.separator", "\n"));
	}

	public static String searchAndReplace(String source, String oldText, String newText, boolean replaceAll,
			boolean ignoreCase, boolean wholeWordsOnly) {
		if (source == null || oldText == null || newText == null || source.equals("") || oldText.equals(""))
			return source;
		List<Integer> matchingIndexes = new ArrayList<Integer>();
		String sourceString = ignoreCase ? source.toLowerCase() : source;
		String oldTextString = ignoreCase ? oldText.toLowerCase() : oldText;
		int oldTextLength = oldTextString.length();
		int pos = 0;
		do {
			pos = sourceString.indexOf(oldTextString, pos);
			if (pos == -1)
				break;
			matchingIndexes.add(new Integer(pos));
			if (!replaceAll)
				break;
			pos += oldTextLength;
		} while (true);
		if (matchingIndexes.size() == 0)
			return source;
		if (wholeWordsOnly) {
			Iterator<Integer> it = (new ArrayList<Integer>(matchingIndexes)).iterator();
			do {
				if (!it.hasNext())
					break;
				pos = ((Integer) it.next()).intValue();
				if (pos > 0) {
					char ch = source.charAt(pos - 1);
					if (Character.isLetterOrDigit(ch)) {
						matchingIndexes.remove(new Integer(pos));
						continue;
					}
				}
				if (pos + oldText.length() < source.length()) {
					char ch = source.charAt(pos + oldText.length());
					if (Character.isLetterOrDigit(ch))
						matchingIndexes.remove(new Integer(pos));
				}
			} while (true);
		}
		StringBuffer result = new StringBuffer();
		int startPos = 0;
		for (Iterator<Integer> it = matchingIndexes.iterator(); it.hasNext();) {
			pos = ((Integer) it.next()).intValue();
			result.append(source.substring(startPos, pos));
			result.append(newText);
			startPos = pos + oldText.length();
		}

		if (startPos < source.length())
			result.append(source.substring(startPos, source.length()));
		return result.toString();
	}

	public static String searchAndReplace(String source, String oldString, String newString) {
		StringBuffer result = new StringBuffer();
		if (source == null)
			return source;
		int oldStringLength = oldString.length();
		do {
			int pos = source.indexOf(oldString);
			if (pos == -1) {
				result.append(source);
				break;
			}
			result.append(source.substring(0, pos));
			result.append(newString);
			source = source.substring(pos + oldStringLength, source.length());
		} while (true);
		return result.toString();
	}

	public static String searchAndReplaceWholeWords(String source, String oldString, String newString) {
		StringBuffer result = new StringBuffer();
		boolean isWholeWord = true;
		if (source == null)
			return source;
		int oldStringLength = oldString.length();
		do {
			int pos = source.indexOf(oldString);
			if (pos == -1) {
				result.append(source);
				break;
			}
			if (pos > 0) {
				char ch = source.charAt(pos - 1);
				if (!Character.isLetterOrDigit(ch) && ch != '.')
					isWholeWord = true;
				else
					isWholeWord = false;
			}
			if (isWholeWord && pos + oldStringLength < source.length()) {
				char ch = source.charAt(pos + oldStringLength);
				if (Character.isLetterOrDigit(ch) || ch == '.')
					isWholeWord = false;
			}
			if (isWholeWord) {
				result.append(source.substring(0, pos));
				result.append(newString);
			} else {
				result.append(source.substring(0, pos + oldStringLength));
			}
			source = source.substring(pos + oldStringLength, source.length());
			isWholeWord = false;
		} while (true);
		return result.toString();
	}

	public static String getSubstring(String str, String s1, String s2) {
		int pos1;
		if (s1 == null)
			pos1 = -1;
		else
			pos1 = str.indexOf(s1);
		int pos2;
		if (s2 == null)
			pos2 = -1;
		else
			pos2 = str.indexOf(s2);
		if (pos1 >= 0 && pos2 >= 0)
			return str.substring(pos1 + s1.length(), pos2);
		if (pos1 >= 0)
			return str.substring(pos1 + s1.length());
		if (pos2 >= 0)
			return str.substring(0, pos2);
		else
			return null;
	}

	public static int countOccurrences(String string, char c) {
		int count = 0;
		for (int index = string.indexOf(c); index != -1;) {
			index = string.indexOf(c, index + 1);
			count++;
		}

		return count;
	}

	public static String insertLineNumbers(String string) {
		StringBuffer sb = new StringBuffer(string.length());
		int count = countOccurrences(string, '\n');
		count = String.valueOf(count).length();
		int lineNum = 1;
		int start = 0;
		for (int index = string.indexOf('\n'); index != -1; index = string.indexOf('\n', start)) {
			String line = string.substring(start, index + 1);
			String num = pad(String.valueOf(lineNum), ' ', count);
			sb.append((new StringBuilder()).append(num).append(": ").toString());
			sb.append(line);
			lineNum++;
			start = index + 1;
		}

		if (string.substring(start, string.length()).length() > 0) {
			String num = pad(String.valueOf(lineNum), ' ', count);
			sb.append((new StringBuilder()).append(num).append(": ").toString());
			sb.append(string.substring(start, string.length()));
		}
		return sb.toString();
	}

	public static String replaceTabsWithSpaces(String inputString, int spaceCount) {
		char spaces[] = new char[spaceCount];
		Arrays.fill(spaces, ' ');
		return searchAndReplace(inputString, "\t", new String(spaces));
	}

	public static String pad(String inputString, char padChar, int minimumSize) {
		if (inputString.length() < minimumSize) {
			char cbuf[] = new char[minimumSize - inputString.length()];
			Arrays.fill(cbuf, padChar);
			inputString = (new StringBuilder()).append(new String(cbuf)).append(inputString).toString();
		}
		return inputString;
	}

	public static String toReadableString(String str) {
		StringBuffer newStr = new StringBuffer(str.length());
		for (int i = 0; i != str.length(); i++) {
			int charValue = str.charAt(i);
			if (charValue >= 32 && charValue <= 127)
				newStr.append(str.charAt(i));
			else
				newStr.append((new StringBuilder()).append("\\(").append(charValue).append(")").toString());
		}

		return newStr.toString();
	}

	public static byte[] hexStringToByteArray(String hex) {
		List<Byte> list = new ArrayList<Byte>();
		int v;
		for (StringTokenizer tokenizer = new StringTokenizer(hex, " "); tokenizer.hasMoreTokens(); list.add(new Byte((byte) v))) {
			v = Integer.parseInt(tokenizer.nextToken(), 16);
			if (v < 0 || v > 255)throw new NumberFormatException();
		}
		byte data[] = new byte[list.size()];
		for (int i = 0; i < list.size(); i++){
			data[i] = ((Byte) list.get(i)).byteValue();
		}
		return data;
	}

	public static String byteArrayToHexString(byte bytes[]) {
		return byteArrayToHexString(bytes, true);
	}

	public static String byteArrayToHexString(byte bytes[], boolean spaceBetweenHexNumbers) {
		StringBuilder res = new StringBuilder(bytes.length * (spaceBetweenHexNumbers ? 3 : 2));
		for (int i = 0; i < bytes.length; i++) {
			int value = bytes[i];
			if (value < 0)
				value += 256;
			int high = value / 16;
			int low = value - high * 16;
			res.append(getHexChar(high)).append(getHexChar(low));
			if (spaceBetweenHexNumbers && i + 1 < bytes.length)
				res.append(' ');
		}

		return res.toString();
	}

	private static char getHexChar(int value) {
		return HEX_DIGITS[value];
	}

	public static int indexOfFirstWhitespace(String s) {
		for (int i = 0; i < s.length(); i++)
			if (Character.isWhitespace(s.charAt(i)))
				return i;

		return -1;
	}

	public static int indexOfFirstNonWhitespace(String s) {
		for (int i = 0; i < s.length(); i++)
			if (!Character.isWhitespace(s.charAt(i)))
				return i;

		return -1;
	}

	public static String removeNonPrintableCharacters(String s) {
		if (s == null)
			return null;
		int length = s.length();
		int i = 0;
		do {
			if (i >= length)
				break;
			char c = s.charAt(i);
			if ((c < ' ' || c > '\uD7FF')
				&& c != ' '
				&& (c < '\uE000' || c > '\uFFFD')
				&& c != '\t'
				&& c != '\n'
				&& c != '\r')
				break;
			i++;
		} while (true);
		if (i < length) {
			StringBuilder sb = new StringBuilder(length - 1);
			sb.append(s, 0, i);
			for (; i < length; i++) {
				char c = s.charAt(i);
				if (c >= ' '
					&& c <= '\uD7FF'
					|| c == ' '
					|| c >= '\uE000'
					&& c <= '\uFFFD'
					|| c == '\t'
					|| c == '\n'
					|| c == '\r')
					sb.append(c);
			}

			return sb.toString();
		} else {
			return s;
		}
	}

	public static void appendPrintableCharacters(StringBuilder sb, String s) {
		if (s == null) {
			sb.append(s);
		} else {
			int length = s.length();
			for (int i = 0; i < length; i++) {
				char c = s.charAt(i);
				if (c >= ' '
					&& c <= '\uD7FF'
					|| c == ' '
					|| c >= '\uE000'
					&& c <= '\uFFFD'
					|| c == '\t'
					|| c == '\n'
					|| c == '\r')
					sb.append(c);
			}

		}
	}

	public static boolean isPrintableCharacter(char c) {
		return c >= ' '
				&& c <= '\uD7FF'
				|| c == ' '
				|| c >= '\uE000'
				&& c <= '\uFFFD'
				|| c == '\t'
				|| c == '\n'
				|| c == '\r';
	}

	public static String removeTabsBackspaceAndLineBreaks(String s) {
		int length = s.length();
		int i = 0;
		do {
			if (i >= length)
				break;
			char c = s.charAt(i);
			if (c == '\t' || c == '\n' || c == '\r' || c == '\b')
				break;
			i++;
		} while (true);
		if (i < length) {
			StringBuilder sb = new StringBuilder(length - 1);
			sb.append(s, 0, i);
			for (; i < length; i++) {
				char c = s.charAt(i);
				if (c != '\t' && c != '\n' && c != '\r' && c != '\b')
					sb.append(c);
			}

			return sb.toString();
		} else {
			return s;
		}
	}

	public static boolean isEmptyOrWhitespaceOnly(String s) {
		for (int i = 0; i < s.length(); i++)
			if (!Character.isWhitespace(s.charAt(i)))
				return false;

		return true;
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static void appendAsciiAlphanumeric(StringBuilder sb, String s) {
		try {
			appendAsciiAlphaNumeric(sb, s);
		} catch (IOException e) {
			throw new RuntimeException("Unexpected 	IO error.", e);
		}
	}

	public static void appendAsciiAlphaNumeric(Appendable appendable, String s) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
				appendable.append(c);
		}

	}

	public static String retainAsciiAlphaNumeric(String s) {
		StringBuilder sb = new StringBuilder();
		appendAsciiAlphanumeric(sb, s);
		return sb.toString();
	}

	public static final char NBSP = 160;
	private static DecimalFormat NO_SCIENTIFIC_DOUBLE_FORMATTER;
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
			'C', 'D', 'E', 'F' };

	static {
		NO_SCIENTIFIC_DOUBLE_FORMATTER = new DecimalFormat("0.#", new DecimalFormatSymbols(new Locale("en",
				"US")));
		NO_SCIENTIFIC_DOUBLE_FORMATTER.setMaximumFractionDigits(325);
	}
	
	public static void main(String[] args) {
		String str = "disp|bbs.asp?boar\\dID=8&ID=2056&page=1564";
		System.out.println(buildJSRegex(str));
	}

}
