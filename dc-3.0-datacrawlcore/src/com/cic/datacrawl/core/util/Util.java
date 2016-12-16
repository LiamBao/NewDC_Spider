package com.cic.datacrawl.core.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

//Referenced classes of package kapow.util:
//         CollectText, StringUtil, IOUtil, ObjectUtil, 
//         ClassUtil

public final class Util {

	public Util() {
	}

	public static String htmlEncode(int ch) {
		if (_inverseCharRefs == null) {
			_inverseCharRefs = new Hashtable(_charRefs.size());
			Object key;
			for (Enumeration keys = _charRefs.keys(); keys.hasMoreElements(); _inverseCharRefs.put(_charRefs
					.get(key), key))
				key = keys.nextElement();

		}
		return (String) _inverseCharRefs.get(new Integer(ch));
	}

	public static Locale convertToLocale(String localeString) {
		int index1 = localeString.indexOf('_');
		if (index1 != -1) {
			int index2 = localeString.substring(index1 + 1).indexOf('_');
			if (index2 != -1)
				return new Locale(localeString.substring(0, index1), localeString.substring(index1 + 1,
																							index2),
						localeString.substring(index2 + 1));
			else
				return new Locale(localeString.substring(0, index1), localeString.substring(index1 + 1));
		} else {
			return new Locale(localeString, "");
		}
	}

	public static String getRef(String ref) {
		if (ref.equals("#032"))
			return " ";
		if (ref.equals("aelig"))
			return "\346";
		if (ref.equals("AElig"))
			return "\306";
		if (ref.equals("oslash"))
			return "\370";
		if (ref.equals("Oslash"))
			return "\330";
		if (ref.equals("aring"))
			return "\345";
		if (ref.equals("Aring"))
			return "\305";
		if (ref.equals("ouml"))
			return "\366";
		if (ref.equals("Ouml"))
			return "\326";
		if (ref.equals("auml"))
			return "\344";
		if (ref.equals("Auml"))
			return "\304";
		if (ref.equals("eacute"))
			return "\351";
		if (ref.equals("nbsp"))
			return " ";
		if (ref.equals("amp"))
			return "&";
		else
			return "_";
	}

	public static int parseInt(String number) {
		number = number.trim();
		boolean negative = number.length() > 0 && number.charAt(0) == '-';
		StringBuffer resultNumber = new StringBuffer(number.length());
		int lastComma = Math.max(number.lastIndexOf(','), number.lastIndexOf('.'));
		lastComma = Math.max(lastComma, number.lastIndexOf(':'));
		int lastSeparator = -1;
		for (int i = 0; i < number.length(); i++) {
			char next = number.charAt(i);
			if (next >= '0' && next <= '9') {
				resultNumber.append(next);
				continue;
			}
			if (i == lastComma)
				lastSeparator = resultNumber.length();
		}

		int result = -9999;
		if (resultNumber.length() > 0)
			try {
				int round = 0;
				if (lastSeparator != -1 && resultNumber.length() - lastSeparator != 3) {
					if (resultNumber.length() - lastSeparator > 0 && resultNumber.charAt(lastSeparator) > '4')
						round = 1;
					resultNumber.setLength(lastSeparator);
				}
				result = Integer.parseInt(resultNumber.toString()) + round;
				if (negative)
					result *= -1;
			} catch (NumberFormatException e) {
			}
		return result;
	}

	public static int getIntNumber(String number) {
		String num = number.trim();
		StringBuffer resultInt = new StringBuffer();
		boolean negative = num.length() > 0 && num.charAt(0) == '-';
		for (int i = 0; i < num.length(); i++) {
			char next = num.charAt(i);
			if (next >= '0' && next <= '9')
				resultInt.append(next);
		}

		int result = -9999;
		if (resultInt.length() > 0)
			try {
				result = Integer.parseInt(resultInt.toString());
				if (negative)
					result *= -1;
			} catch (NumberFormatException e) {
			}
		return result;
	}

	public static double getDoubleNumber(String num) {
		num = num.trim();
		StringBuffer resultDouble = new StringBuffer();
		boolean negative = num.length() > 0 && num.charAt(0) == '-';
		char decimalPoint = ',';
		if (num.indexOf(",") == -1)
			decimalPoint = '.';
		boolean decimalFound = false;
		for (int i = 0; i < num.length(); i++) {
			char next = num.charAt(i);
			if (next >= '0' && next <= '9')
				resultDouble.append(next);
			if (resultDouble.length() > 0 && next == decimalPoint && !decimalFound) {
				resultDouble.append(".");
				decimalFound = true;
			}
		}

		double result = -999.99900000000002D;
		if (resultDouble.length() > 0)
			try {
				result = Double.valueOf(resultDouble.toString()).doubleValue();
				if (negative)
					result *= -1D;
			} catch (NumberFormatException e) {
			}
		return result;
	}

	public static boolean isHTMLWhitespace(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == '\u200B';
	}

	public static String trimHTMLWhitespace(String string) {
		int start;
		for (start = 0; start < string.length() && isHTMLWhitespace(string.charAt(start)); start++)
			;
		int end;
		for (end = string.length() - 1; end >= 0 && isHTMLWhitespace(string.charAt(end)); end--)
			;
		if (start <= end)
			return string.substring(start, end + 1);
		else
			return "";
	}

	public static void sleep(long milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
		}
	}

	public static String capitalize(String str) {
		return StringUtil.capitalizeString(str);
	}

	public static String capitalizeFirstChar(String str) {
		return StringUtil.capitalizeFirstChar(str);
	}

	public static String toCSV(String stringArray[]) {
		return StringUtil.toCSV(stringArray);
	}

	public static String[] fromCSV(String csv) {
		return StringUtil.fromCSV(csv);
	}

	public static String removeSpaces(String string) {
		return StringUtil.removeSpaces(string);
	}

	public static String collapseSpaces(String string) {
		return StringUtil.collapseSpaces(string);
	}

	public static String lineWrapText(String text, int charsPerLine) {
		return StringUtil.lineWrapText(text, charsPerLine, -1);
	}

	public static String searchAndReplace(String source, String oldString, String newString) {
		return StringUtil.searchAndReplace(source, oldString, newString);
	}

	public static String searchAndReplaceWholeWords(String source, String oldString, String newString) {
		return StringUtil.searchAndReplaceWholeWords(source, oldString, newString);
	}

	public static String getSubstring(String str, String s1, String s2) {
		return StringUtil.getSubstring(str, s1, s2);
	}

	public static String toReadableString(String str) {
		return StringUtil.toReadableString(str);
	}

	public static final byte BYTE_NULL = -128;
	public static final int INT_NULL = -9999;
	public static final long LONG_NULL = 0x8000000000000000L;
	public static final double DOUBLE_NULL = -999.99900000000002D;
	public static final int _epoch = 1930;
	private static final Hashtable _charRefs;
	private static Hashtable _inverseCharRefs = null;

	static {
		_charRefs = new Hashtable(200);
		_charRefs.put("nbsp", new Integer(32));
		_charRefs.put("quot", new Integer(34));
		_charRefs.put("amp", new Integer(38));
		_charRefs.put("gt", new Integer(62));
		_charRefs.put("lt", new Integer(60));
		_charRefs.put("iexcl", new Integer(161));
		_charRefs.put("cent", new Integer(162));
		_charRefs.put("pound", new Integer(163));
		_charRefs.put("curren", new Integer(164));
		_charRefs.put("yen", new Integer(165));
		_charRefs.put("brvbar", new Integer(166));
		_charRefs.put("sect", new Integer(167));
		_charRefs.put("uml", new Integer(168));
		_charRefs.put("copy", new Integer(169));
		_charRefs.put("ordf", new Integer(170));
		_charRefs.put("laquo", new Integer(171));
		_charRefs.put("not", new Integer(172));
		_charRefs.put("shy", new Integer(173));
		_charRefs.put("reg", new Integer(174));
		_charRefs.put("macr", new Integer(175));
		_charRefs.put("deg", new Integer(176));
		_charRefs.put("plusmn", new Integer(177));
		_charRefs.put("sup2", new Integer(178));
		_charRefs.put("sup3", new Integer(179));
		_charRefs.put("acute", new Integer(180));
		_charRefs.put("micro", new Integer(181));
		_charRefs.put("para", new Integer(182));
		_charRefs.put("middot", new Integer(183));
		_charRefs.put("cedil", new Integer(184));
		_charRefs.put("sup1", new Integer(185));
		_charRefs.put("ordm", new Integer(186));
		_charRefs.put("raquo", new Integer(187));
		_charRefs.put("frac14", new Integer(188));
		_charRefs.put("frac12", new Integer(189));
		_charRefs.put("frac34", new Integer(190));
		_charRefs.put("iquest", new Integer(191));
		_charRefs.put("Agrave", new Integer(192));
		_charRefs.put("Aacute", new Integer(193));
		_charRefs.put("Acirc", new Integer(194));
		_charRefs.put("Atilde", new Integer(195));
		_charRefs.put("Auml", new Integer(196));
		_charRefs.put("Aring", new Integer(197));
		_charRefs.put("AElig", new Integer(198));
		_charRefs.put("Ccedil", new Integer(199));
		_charRefs.put("Egrave", new Integer(200));
		_charRefs.put("Eacute", new Integer(201));
		_charRefs.put("Ecirc", new Integer(202));
		_charRefs.put("Euml", new Integer(203));
		_charRefs.put("Igrave", new Integer(204));
		_charRefs.put("Iacute", new Integer(205));
		_charRefs.put("Icirc", new Integer(206));
		_charRefs.put("Iuml", new Integer(207));
		_charRefs.put("ETH", new Integer(208));
		_charRefs.put("Ntilde", new Integer(209));
		_charRefs.put("Ograve", new Integer(210));
		_charRefs.put("Oacute", new Integer(211));
		_charRefs.put("Ocirc", new Integer(212));
		_charRefs.put("Otilde", new Integer(213));
		_charRefs.put("Ouml", new Integer(214));
		_charRefs.put("times", new Integer(215));
		_charRefs.put("Oslash", new Integer(216));
		_charRefs.put("Ugrave", new Integer(217));
		_charRefs.put("Uacute", new Integer(218));
		_charRefs.put("Ucirc", new Integer(219));
		_charRefs.put("Uuml", new Integer(220));
		_charRefs.put("Yacute", new Integer(221));
		_charRefs.put("THORN", new Integer(222));
		_charRefs.put("szlig", new Integer(223));
		_charRefs.put("agrave", new Integer(224));
		_charRefs.put("aacute", new Integer(225));
		_charRefs.put("acirc", new Integer(226));
		_charRefs.put("atilde", new Integer(227));
		_charRefs.put("auml", new Integer(228));
		_charRefs.put("aring", new Integer(229));
		_charRefs.put("aelig", new Integer(230));
		_charRefs.put("ccedil", new Integer(231));
		_charRefs.put("egrave", new Integer(232));
		_charRefs.put("eacute", new Integer(233));
		_charRefs.put("ecirc", new Integer(234));
		_charRefs.put("euml", new Integer(235));
		_charRefs.put("igrave", new Integer(236));
		_charRefs.put("iacute", new Integer(237));
		_charRefs.put("icirc", new Integer(238));
		_charRefs.put("iuml", new Integer(239));
		_charRefs.put("eth", new Integer(240));
		_charRefs.put("ntilde", new Integer(241));
		_charRefs.put("ograve", new Integer(242));
		_charRefs.put("oacute", new Integer(243));
		_charRefs.put("ocirc", new Integer(244));
		_charRefs.put("otilde", new Integer(245));
		_charRefs.put("ouml", new Integer(246));
		_charRefs.put("divide", new Integer(247));
		_charRefs.put("oslash", new Integer(248));
		_charRefs.put("ugrave", new Integer(249));
		_charRefs.put("uacute", new Integer(250));
		_charRefs.put("ucirc", new Integer(251));
		_charRefs.put("uuml", new Integer(252));
		_charRefs.put("yacute", new Integer(253));
		_charRefs.put("thorn", new Integer(254));
		_charRefs.put("yuml", new Integer(255));
		_charRefs.put("euro", new Integer(8364));
	}
}
