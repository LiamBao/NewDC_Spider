package com.cic.datacrawl.core.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class DateUtil {
	public static final long PROCESS_START_TIME = System.currentTimeMillis();

	public static final String formatDate(Date date) {
		if (date == null)
			return null;
		return DATE_FORMATER.format(date);
	}

	public static int calcDays(Date startDate) {
		return calcDays(startDate, new Date());
	}

	public static int calcDays(Date startDate, Date endDate) {
		if (startDate == null) {
			throw new NullPointerException("Invalid argument: startDate is null");
		}
		if (endDate == null) {
			throw new NullPointerException("Invalid argument: endDate is null");
		}
		long oneDay = 3600 * 24 * 1000;
		long millis = endDate.getTime() - startDate.getTime();
		return new Double(millis / oneDay).intValue();
	}

	public static final String format(Date date, String format) {
		if (date == null)
			return null;
		SimpleDateFormat formater = null;
		if (format == null)
			formater = DATETIMESTAMP_FORMATER;
		formater = new SimpleDateFormat(format);
		try {
			return formater.format(date);
		} catch (Exception e) {
			try {
				return formatDate(date);
			} catch (Exception e1) {
				try {
					return formatDatetime(date);
				} catch (Exception e2) {
				}
				try {
					return formatTimestamp(date);
				} catch (Exception e3) {
					return date.toString();
				}
			}
		}
	}

	public static final String formatDatetime(Date date) {
		if (date == null)
			return null;
		return DATETIME_FORMATER.format(date);
	}

	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATETIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String formatTimestamp(Date date) {
		if (date == null)
			return null;
		return DATETIMESTAMP_FORMATER.format(date);
	}

	public static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat(DATE_FORMAT);
	public static final SimpleDateFormat DATETIME_FORMATER = new SimpleDateFormat(DATETIME_FORMAT);
	public static final SimpleDateFormat DATETIMESTAMP_FORMATER = new SimpleDateFormat(DATETIMESTAMP_FORMAT);
	private static final SimpleDateFormat GMT_DATETIME_FORMATER = new SimpleDateFormat(
			"E, dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);

	public static final String formatGMTString(Date date) {
		if (date == null)
			return null;

		return GMT_DATETIME_FORMATER.format(date) + " GMT";
	}

	public static final long ONE_DAY_MILLISECONDS = 24 * 3600 * 1000;

	private static int maxMonthsAhead = 0;
	private static int maxDaysAhead = 0;

	public static final String[][] MONTH_LANGUAGE = {
			{ "november", "december", "january", "february", "march", "april", "may", "june", "july",
					"august", "september", "october" },
			{ "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" },
			{ "十一月", "十二月", "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月" },
			{ "拾壹月", "拾贰月", "壹月", "贰月", "叁月", "肆月", "伍月", "陆月", "柒月", "捌月", "玖月", "拾月" } };

	public static final String[] MONTH_NUM = { "11-", "12-", "01-", "02-", "03-", "04-", "05-", "06-", "07-",
			"08-", "09-", "10-" };

	private static final List<String> dateFormatList = new ArrayList<String>();

	public static final String[][] DATE_LANGUAGE = {
			{ "31th", "30th", "29th", "28th", "27th", "26th", "25th", "24th", "23th", "22th", "21th", "20th",
					"19th", "18th", "17th", "16th", "15th", "14th", "13th", "12th", "11th", "10th", "9th",
					"8th", "7th", "6th", "5th", "4th", "3th", "2th", "1th" },

			{ "卅一日", "卅日", "廿九日", "廿八日", "廿七日", "廿六日", "廿五日", "廿四日", "廿三日", "廿二日", "廿一日", "廿日", "拾九日", "拾八日",
					"拾七日", "拾六日", "拾五日", "拾四日", "拾三日", "拾二日", "拾一日", "拾日" },
			{ "卅壹日", "", "廿玖日", "廿捌日", "廿柒日", "廿陆日", "廿伍日", "廿肆日", "廿叁日", "廿贰日", "廿壹日", "", "拾玖日", "拾捌日",
					"拾柒日", "拾陆日", "拾伍日", "拾肆日", "拾叁日", "拾贰日", "拾壹日" },
			{ "叁拾一日", "叁拾日", "贰拾九日", "贰拾八日", "贰拾七日", "贰拾六日", "贰拾五日", "贰拾四日", "贰拾三日", "贰拾二日", "贰拾一日", "贰拾" },
			{ "叁拾壹日", "", "贰拾玖日", "贰拾捌日", "贰拾柒日", "贰拾陆日", "贰拾伍日", "贰拾肆日", "贰拾叁日", "贰拾贰日", "贰拾壹日" },
			{ "叁十一日", "叁十日", "贰十九日", "贰十八日", "贰十七日", "贰十六日", "贰十五日", "贰十四日", "贰十三日", "贰十二日", "贰十一日", "贰十" },
			{ "叁十壹日", "", "贰十玖日", "贰十捌日", "贰十柒日", "贰十陆日", "贰十伍日", "贰十肆日", "贰十叁日", "贰十贰日", "贰十壹日" },
			{ "三拾一日", "三拾日", "二拾九日", "二拾八日", "二拾七日", "二拾六日", "二拾五日", "二拾四日", "二拾三日", "二拾二日", "二拾一日", "二拾" },
			{ "三拾壹日", "", "二拾玖日", "二拾捌日", "二拾柒日", "二拾陆日", "二拾伍日", "二拾肆日", "二拾叁日", "二拾贰日", "二拾壹日" },
			{ "三十一日", "三十日", "二十九日", "二十八日", "二十七日", "二十六日", "二十五日", "二十四日", "二十三日", "二十二日", "二十一日", "二十日",
					"十九日", "十八日", "十七日", "十六日", "十五日", "十四日", "十三日", "十二日", "十一日", "十日", "九日", "八日", "七日",
					"六日", "五日", "四日", "三日", "二日", "一日" },
			{ "三十壹日", "", "二十玖日", "二十捌日", "二十柒日", "二十陆日", "二十伍日", "二十肆日", "二十叁日", "二十贰日", "二十壹日", "", "十玖日",
					"十捌日", "十柒日", "十陆日", "十伍日", "十肆日", "十叁日", "十贰日", "十壹日", "十日", "玖日", "捌日", "柒日", "陆日",
					"伍日", "肆日", "叁日", "贰日", "壹日" } };

	public static final String[] DATE_NUM = { "31", "30", "29", "28", "27", "26", "25", "24", "23", "22",
			"21", "20", "19", "18", "17", "16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5",
			"4", "3", "2", "1" };

	public static final String[][] DAY_LANGUAGE = {
			{ "今天", "今 天", "今　天", "昨天", "昨 天", "昨　天", "前天", "前 天", "前　天" },
			{ "today", "today", "today", "yesterday", "yesterday", "yesterday", "the day before yesterday",
					"the day before yesterday", "the day before yesterday" } };

	public static final int[] DAY_NUM = { 0, 0, 0, -1, -1, -1, -2, -2, -2 };

	private static final String dateExtractor(String dateString) {

		String tempStr = new String(dateString);

		long currentTime = System.currentTimeMillis();

		dateString = replaceChineseNumber(dateString);

		for (int i = 0; i < DAY_LANGUAGE.length; ++i) {
			for (int j = 0; j < DAY_NUM.length; ++j) {
				if (dateString.indexOf(DAY_LANGUAGE[i][j]) >= 0) {
					String date = DATE_FORMATER
							.format(new Timestamp(currentTime + (DAY_NUM[j] * ONE_DAY_MILLISECONDS)));
					dateString = StringUtil.replaceAll(dateString, DAY_LANGUAGE[i][j], date);
				}
			}
		}

		dateString = dateString.replaceAll("上午", "AM");
		dateString = dateString.replaceAll("下午", "PM");
		dateString = dateString.replaceAll("\r?\n", " ");
		dateString = dateString.replaceAll("\\s+", " ");
		dateString = dateString.replaceAll("：", ":");

		String[] timeStrExtractor = new String[] {
				"(\\d{1,4}[年|\\ |\\-|\\\\/]\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}[日|\\ ]\\d{1,2}[:|时|\\.]\\d{1,2}[:|\\.|分]\\d{1,2}[\\.|秒]\\d{1,3})", // 毫秒
				"(\\d{1,4}[年|\\ |\\-|\\\\/]\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}[日|\\ ]\\d{1,2}[:|时|\\.]\\d{1,2}[:|\\.|分]\\d{1,2}秒*)",// 秒
				"(\\d{1,4}[年|\\ |\\-|\\\\/]\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}[日|\\ ]\\d{1,2}[:|时|\\.]\\d{1,2}分*)",// 分
				"(\\d{1,4}[年|\\ |\\-|\\\\/]\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}[日|\\ ]\\d{1,2}时*)",// 时
				"(\\d{1,4}[年|\\ |\\-|\\\\/]\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}日*)",// 日
				"(\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}[日|\\ ]\\d{1,2}[:|时|\\.]\\d{1,2}[:|\\.|分]\\d{1,2}[\\.|秒]\\d{1,3})", // 缺少年份的毫秒
				"(\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}[日|\\ ]\\d{1,2}[:|时|\\.]\\d{1,2}[:|\\.|分]\\d{1,2}秒*)", // 缺少年份的秒
				"(\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}[日|\\ ]\\d{1,2}[:|时|\\.]\\d{1,2}分*)", // 缺少年份的分
				"(\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}[日|\\ ]\\d{1,2}时*)", // 缺少年份的时
				"(\\d{1,2}[月|\\ |\\-|\\\\/]\\d{1,2}日*)" // 缺少年份的日
		};

		String result = "";

		for (int i = 0; i < timeStrExtractor.length && result.length() < 1; i++) {

			try {

				List<String> matches = null;
				Pattern p = Pattern
						.compile(timeStrExtractor[i], Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher matcher = p.matcher(dateString);
				if (matcher.find() && matcher.groupCount() >= 1) {
					matches = new ArrayList<String>();
					for (int j = 1; j <= matcher.groupCount(); j++) {
						String temp = matcher.group(j);
						matches.add(temp);
					}
				} else {
					matches = Collections.emptyList();
				}

				if (matches.size() > 0) {
					result = matches.get(0).trim();
				} else {
					result = "";
				}

			} catch (Exception e) {
				result = "";
			}
		}

		if (result.length() < 1) {
			result = tempStr;

		}

		return result;
	}

	public static final Timestamp format(String dateString) {
		return format(dateString, 8);
	}

	public static final Timestamp format(String dateString, int timeZone) {
		// String dateStr = dateString.toLowerCase();
		// for (int i = 0; i < MONTH_LANGUAGE.length; ++i) {
		// for (int j = 0; j < MONTH_NUM.length; ++j) {
		// dateStr = StringUtil.replaceAll(dateStr, MONTH_LANGUAGE[i][j],
		// MONTH_NUM[j]);
		// }
		// }

		long currentTime = System.currentTimeMillis();

		dateString = replaceChineseNumber(dateString);

		// for (int i = 0; i < DAY_LANGUAGE.length; ++i) {
		// for (int j = 0; j < DAY_NUM.length; ++j) {
		// if (dateString.indexOf(DAY_LANGUAGE[i][j]) >= 0) {
		// String date = DATE_FORMATER
		// .format(new Timestamp(currentTime + (DAY_NUM[j] *
		// ONE_DAY_MILLISECONDS)));
		// dateString = StringUtil.replaceAll(dateString, DAY_LANGUAGE[i][j],
		// date);
		// }
		// }
		// }
		//		
		// System.out.println(dateString);

		Timestamp time = formatTemp(dateString, timeZone);

		// String[] timestampformats = new String[] { DATETIMESTAMP_FORMAT,
		// "yyyy/MM/dd HH:mm:ss.SSS",
		// "yyyy MM dd HH:mm:ss.SSS", DATETIME_FORMAT, "yyyy/MM/dd HH:mm:ss",
		// "yyyy MM dd HH:mm:ss",
		// "yyyy MM dd HH:mm", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm",
		// DATE_FORMAT, "yyyy/MM/dd",
		// "yyyy年MM-dd HH:mm:ss", "yyyy年MM-dd HH.mm.ss", "yyyy年MM-dd HH：mm：ss",
		// "yyyy年MM-dd HH:mm",
		// "yyyy年MM-dd HH.mm", "yyyy年MM-dd HH：mm", "yyyy年MM-dd",
		// "yyyy年MM月dd日 HH:mm:ss",
		// "yyyy年MM月dd日 HH.mm.ss", "yyyy年MM月dd日 HH：mm：ss", "yyyy年MM月dd日 HH:mm",
		// "yyyy年MM月dd日 HH.mm",
		// "yyyy年MM月dd日 HH：mm", "yyyy年MM月dd日", "MM/dd HH:mm:ss.SSS",
		// "MM-dd HH:mm:ss", "MM/dd HH:mm:ss",
		// "MM-dd HH:mm", "MM/dd HH:mm", "MM-dd", "MM/dd", "MM月dd日 HH:mm:ss",
		// "MM月dd日 HH.mm.ss",
		// "MM月dd日 HH：mm：ss", "MM月dd日 HH:mm", "MM月dd日 HH.mm", "MM月dd日 HH：mm",
		// "MM月dd日" };
		//		
		// //1、抽取字符串
		// System.out.println("被抽取出的时间为:"+dateString);
		// String result = dateExtractor(dateString);
		// System.out.println("抽取出的时间为："+result);
		//		
		//		
		// for (int i = 0; i < timestampformats.length && time == null; ++i) {
		// //2、然后作格式化输出
		// time = format(result, timestampformats[i], -13, false);
		// }

		return time;
	}

	public static final void addDateFormat(String dateformat){
		if(dateformat != null && dateformat.trim().length() > 0){
			dateFormatList.add(dateformat);
		}
	}
	public static final void clearAllDateFormat(){
		dateFormatList.clear();
	}
	
	public static final Timestamp dataExtractor(String dataString, String format, int zoneTime) {

		Timestamp time = null;

		TimeZone timeZone = null;
		if (zoneTime > 0) {
			timeZone = TimeZone.getTimeZone("GMT+" + zoneTime);
		} else if (zoneTime < 0) {
			timeZone = TimeZone.getTimeZone("GMT" + zoneTime);
		} else {
			timeZone = TimeZone.getTimeZone("GMT+8");
		}

		DateExtractor extractor = new DateExtractor("en_US", format, maxMonthsAhead, maxDaysAhead, TimeZone
				.getTimeZone("GMT+8"), timeZone, true);
		try {
			Calendar a = extractor.execute(dataString);
			time = new Timestamp(a.getTimeInMillis());
		} catch (ParseException e) {
			
		}
		return time;
	}

	public static final Timestamp formatTemp(String dataString, int zoneTime) {

		Timestamp time = null;

		if (dateFormatList != null) {
			for (int i = 0; i < dateFormatList.size() && time == null; ++i) {
				time = dataExtractor(dataString, dateFormatList.get(i), zoneTime);
			}
		}

		if (time == null) {
			String[] timestampformats = new String[] { DATETIMESTAMP_FORMAT, "yyyy/MM/dd HH:mm:ss.SSS",
					"yyyy MM dd HH:mm:ss.SSS", DATETIME_FORMAT, "yyyy/MM/dd HH:mm:ss", "yyyy MM dd HH:mm:ss",
					"yyyy MM dd HH:mm", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm", DATE_FORMAT, "yyyy/MM/dd",
					"yyyy年MM-dd HH:mm:ss", "yyyy年MM-dd HH.mm.ss", "yyyy年MM-dd HH：mm：ss", "yyyy年MM-dd HH:mm",
					"yyyy年MM-dd HH.mm", "yyyy年MM-dd HH：mm", "yyyy年MM-dd", "yyyy年MM月dd日 HH:mm:ss",
					"yyyy年MM月dd日 HH.mm.ss", "yyyy年MM月dd日 HH：mm：ss", "yyyy年MM月dd日 HH:mm", "yyyy年MM月dd日 HH.mm",
					"yyyy年MM月dd日 HH：mm", "yyyy年MM月dd日", "MM/dd HH:mm:ss.SSS", "MM-dd HH:mm:ss",
					"MM/dd HH:mm:ss", "MM-dd HH:mm", "MM/dd HH:mm", "MM-dd", "MM/dd", "MM月dd日 HH:mm:ss",
					"MM月dd日 HH.mm.ss", "MM月dd日 HH：mm：ss", "MM月dd日 HH:mm", "MM月dd日 HH.mm", "MM月dd日 HH：mm",
					"MM月dd日" };

			for (int i = 0; i < timestampformats.length && time == null; ++i) {
				// 2、然后作格式化输出
				time = dataExtractor(dataString, timestampformats[i], zoneTime);
			}
		}
		return time;
	}

	public static final Timestamp format(String dateString, String format) {
		return format(dateString, format, 8);
	}

	public static final Timestamp format(String dateString, String format, int zoneTime) {
		return format(dateString, format, zoneTime, true);
	}

	public static final String[] IGNORE_CHARS = { "　", "(", ")", "[", "]", "{", "}", "<", ">", "【", "】", "『",
			"』", "（", "）", "《", "》", "﹝", "﹞", "﹛", "﹜", "﹤", "﹥", "＜", "＞", "｛", "｝", "［", "］" };

	private static String replaceChineseNumber(String dateString) {
		long currentTime = System.currentTimeMillis();
		dateString = dateString.trim().toLowerCase();
		for (int i = 0; i < IGNORE_CHARS.length; ++i) {
			dateString = StringUtil.replaceAll(dateString, IGNORE_CHARS[i], "");
		}
		String dateStrBack = dateString.toString();
		boolean needChanged = true;
		for (int i = 0; needChanged && i < MONTH_LANGUAGE.length; ++i) {
			for (int j = 0; needChanged && j < MONTH_NUM.length; ++j) {
				dateString = StringUtil.replaceAll(dateString, MONTH_LANGUAGE[i][j], MONTH_NUM[j]);

				needChanged = dateStrBack.equals(dateString);
			}
		}
		dateStrBack = dateString.toString();
		needChanged = true;
		for (int i = 0; needChanged && i < DATE_LANGUAGE.length; ++i) {
			for (int j = 0; needChanged && j < DATE_LANGUAGE[i].length; ++j) {
				dateString = StringUtil.replaceAll(dateString, DATE_LANGUAGE[i][j], DATE_NUM[j]);

				needChanged = dateStrBack.equals(dateString);
			}
		}

		for (int i = 0; i < DAY_LANGUAGE.length; ++i) {
			for (int j = 0; j < DAY_NUM.length; ++j) {
				if (dateString.indexOf(DAY_LANGUAGE[i][j]) >= 0) {
					String date = DATE_FORMATER
							.format(new Timestamp(currentTime + (DAY_NUM[j] * ONE_DAY_MILLISECONDS)));
					dateString = StringUtil.replaceAll(dateString, DAY_LANGUAGE[i][j], date + " ");
				}
			}
		}
		dateString = dateString.replaceAll("上午", "AM");
		dateString = dateString.replaceAll("下午", "PM");
		return dateString;
	}

	public static final Timestamp format(String dateString, String format, int zoneTime, boolean replaceMonth) {

		if (replaceMonth) {
			dateString = replaceChineseNumber(dateString);
		}

		// while (((byte) dateString.charAt(dateString.length() - 1)) == 14) {
		// dateString = dateString.substring(0, dateString.length() - 1);
		// }
		// while (((byte) dateString.charAt(0) == 14)) {
		// dateString = dateString.substring(1);
		// }
		//		

		Timestamp time = null;
		if (format != null && format.trim().length() > 0)
			time = dataExtractor(dateString, format, zoneTime);

		if (time == null) {
			time = formatTemp(dateString, zoneTime);
		}
		// DateExtractor extractor = new DateExtractor("zh_CN", format,
		// maxMonthsAhead, maxDaysAhead);
		//		
		// try {
		//			
		// Calendar a = extractor.execute(dateString);
		//			
		// time = new Timestamp(a.getTimeInMillis());
		//			
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// //e.printStackTrace();
		// }

		// 如果出错，则采用formatTemp方法进行抽取
		// time = formatTemp(dateString,zoneTime);

		// if (format != null && format.trim().length() > 0) {
		// long longTime = 0;
		// String dateStr = null;
		// int year = -1;
		// if (format.indexOf("y") < 0) {
		// format += "-yyyy";
		//
		// if (zoneTime >= -12 && zoneTime <= 12) {
		// year = Calendar.getInstance(new SimpleTimeZone(zoneTime * 3600 *
		// 1000, ""))
		// .get(Calendar.YEAR);
		// } else {
		// year = Calendar.getInstance().get(Calendar.YEAR);
		// }
		//
		// dateStr = dateString + "-" + year;
		// } else {
		// dateStr = dateString;
		// }
		//
		// SimpleDateFormat formater = new SimpleDateFormat(format);
		//
		// if (zoneTime >= -12 && zoneTime <= 12) {
		// formater.setTimeZone(new SimpleTimeZone(zoneTime * 3600 * 1000, ""));
		// }
		//
		// try {
		// longTime = formater.parse(dateStr).getTime();
		// } catch (Exception e) {
		// }
		//
		// if (longTime > 0 && longTime > System.currentTimeMillis() && year >
		// 1000) {
		// dateStr = dateString + "-" + (year - 1);
		// try {
		// longTime = formater.parse(dateStr).getTime();
		// } catch (Exception e) {
		// }
		// }
		// if (longTime > 0) {
		// time = new Timestamp(longTime);
		// }
		// }

		return time;
	}
}
