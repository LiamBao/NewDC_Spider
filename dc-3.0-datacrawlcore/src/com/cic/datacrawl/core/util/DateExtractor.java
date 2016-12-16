package com.cic.datacrawl.core.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DateExtractor {

	public DateExtractor(String localeString, String pattern, int maxMonthsAhead, int maxDaysAhead) {
		this(localeString, pattern, maxMonthsAhead, maxDaysAhead, null, null, false);
	}

	public DateExtractor(String localeString, String pattern, int maxMonthsAhead, int maxDaysAhead,
			TimeZone fromTimeZone, TimeZone toTimeZone, boolean overrideInputTimeZone) {
		
		_pattern = pattern;
		_maxMonthsAhead = maxMonthsAhead;
		_maxDaysAhead = maxDaysAhead;
		_fromTimeZone = fromTimeZone;
		_toTimeZone = toTimeZone;
		_overrideInputTimeZone = overrideInputTimeZone;
		_pattern = _pattern.trim();
		_symbols = new DateFormatSymbols(Util.convertToLocale(localeString));
		String months[] = _symbols.getMonths();
		String shortMonths[] = _symbols.getShortMonths();
		_upperCasedMonthNames = new String[months.length];
		_upperCasedShortMonthNames = new String[shortMonths.length];
		for (int i = 0; i < months.length; i++) {
			_upperCasedMonthNames[i] = months[i].toUpperCase();
			_upperCasedShortMonthNames[i] = shortMonths[i].toUpperCase();
		}

		_minimumMonthNameLength = getMinimumUniqueNameLength(_upperCasedMonthNames);
		_months = new Hashtable();
		for (int i = 0; i < _upperCasedMonthNames.length; i++) {
			Integer value = new Integer(i + 1);
			if (!areNonEmpty(_upperCasedMonthNames[i], _upperCasedShortMonthNames[i]))
				continue;
			for (Iterator iterator = getUpperCasedPrefixes(_upperCasedMonthNames[i],
															_upperCasedShortMonthNames[i],
															_minimumMonthNameLength).iterator(); iterator
					.hasNext(); _months.put(iterator.next(), value))
				;
		}

		String amPmStrings[] = _symbols.getAmPmStrings();
		_amString = amPmStrings[0];
		_pmString = amPmStrings[1];
		_compiledWeekdayPatterns = new Pattern[7];
	}

	public Calendar execute(String inputString) throws ParseException {
		return execute(inputString, null);
	}

	public Calendar execute(String inputString, Calendar basisCalendar) throws ParseException {
		inputString = inputString.trim();
		inputString = inputString.toUpperCase();
		synchronized (this) {
			if (_compiledPattern == null)
				compile();
		}
		if (_weekdayGroupNumber >= 0
			&& _dateGroupNumber < 0
			&& (_monthGroupNumber >= 0 || _yearGroupNumber >= 0))
			throw new ParseException(
					"Cannot use 'EEE' or 'EEEE' in combination with 'MM' or 'yyy' without 'dd'.", 0);
		if (_dateGroupNumber < 0
			&& _monthGroupNumber < 0
			&& _yearGroupNumber < 0
			&& _hoursGroupNumber < 0
			&& _minutesGroupNumber < 0
			&& _secondsGroupNumber < 0
			&& _weekdayGroupNumber < 0) {
			if (_amPmGroupNumber >= 0)
				throw new ParseException("Cannot use 'a' without additional date information.", 0);
			if (_timeZoneGroupNumber >= 0)
				throw new ParseException("Cannot use 'Z' without additional date information.", 0);
			else
				throw new ParseException("Cannot extract date. The pattern is insufficient.", 0);
		}
		boolean hasYear = _yearGroupNumber >= 0;
		boolean hasMonthOrCoarser = _monthGroupNumber >= 0 || hasYear;
		boolean hasDayOrCoarser = _dateGroupNumber >= 0 || _weekdayGroupNumber >= 0 || hasMonthOrCoarser;
		boolean hasHoursOrCoarser = _hoursGroupNumber >= 0 || hasDayOrCoarser;
		boolean hasMinutesOrCoarser = _minutesGroupNumber >= 0 || hasHoursOrCoarser;
		Matcher matcher = _compiledPattern.matcher(inputString);
		matcher.reset();
		Calendar extract = getBasisCalendar(basisCalendar);
		extract.set(Calendar.MILLISECOND, 0);
		if (matcher.find()) {
			int hours = extract.get(Calendar.HOUR_OF_DAY);
			if (_hoursGroupNumber >= 0) {
				String hourStr = matcher.group(_hoursGroupNumber);
				try {
					hours = Integer.parseInt(hourStr);
				} catch (NumberFormatException e) {
				}
				if (hours < 0 || hours > 23)
					throw new ParseException((new StringBuilder()).append("Invalid hour: ").append(hours)
							.toString(), 0);
			} else if (hasDayOrCoarser)
				hours = 0;
			if (_amPmGroupNumber >= 0) {
				String ampmStr = matcher.group(_amPmGroupNumber);
				if (hours > 12)
					throw new ParseException((new StringBuilder()).append("Invalid hour: ").append(hours)
							.append(" ").append(ampmStr).toString(), 0);
				if (isAmString(ampmStr) && hours == 12)
					hours = 0;
				else if (isPmString(ampmStr) && hours < 12)
					hours += 12;
			}
			extract.set(Calendar.HOUR_OF_DAY, hours);
			int minutes = extract.get(Calendar.MINUTE);
			if (_minutesGroupNumber >= 0) {
				String minuteStr = matcher.group(_minutesGroupNumber);
				try {
					minutes = Integer.parseInt(minuteStr);
				} catch (NumberFormatException e) {
				}
				if (minutes < 0 || minutes > 59)
					throw new ParseException((new StringBuilder("Invalid minutes: ")).append(minutes)
							.toString(), 0);
			} else if (hasHoursOrCoarser)
				minutes = 0;
			extract.set(Calendar.MINUTE, minutes);
			int seconds = extract.get(Calendar.SECOND);
			if (_secondsGroupNumber >= 0) {
				String secondStr = matcher.group(_secondsGroupNumber);
				try {
					seconds = Integer.parseInt(secondStr);
				} catch (NumberFormatException e) {
				}
				if (seconds < 0 || seconds > 59)
					throw new ParseException((new StringBuilder()).append("Invalid seconds: ")
							.append(seconds).toString(), 0);
			} else if (hasMinutesOrCoarser)
				seconds = 0;
			extract.set(Calendar.SECOND, seconds);
			if (_weekdayGroupNumber >= 0 && _dateGroupNumber < 0) {
				String weekdayStr = matcher.group(_weekdayGroupNumber);
				int weekday;
				for (weekday = 0; weekday < 7
									&& !_compiledWeekdayPatterns[weekday].matcher(weekdayStr).matches(); weekday++) {
				}
				if (weekday == 7)
					throw new ParseException((new StringBuilder())
							.append("Internal error in Date Extractor (weekdayStr = \"").append(weekdayStr)
							.append("\")").toString(), 0);
				while (extract.get(Calendar.DAY_OF_WEEK) != _weekdayValues[weekday]) {
					extract.add(Calendar.DAY_OF_WEEK, 1);
				}
			} else {
				int day = extract.get(Calendar.DAY_OF_MONTH);
				if (_dateGroupNumber >= 0) {
					String dateStr = matcher.group(_dateGroupNumber);
					try {
						day = Integer.parseInt(dateStr);
					} catch (NumberFormatException e) {
					}
					if (day < 1 || day > 31)
						throw new ParseException((new StringBuilder()).append("Invalid day: ").append(day)
								.toString(), 0);
				} else if (hasMonthOrCoarser)
					day = 1;
				extract.set(Calendar.DAY_OF_MONTH, day);
				if (_monthGroupNumber >= 0) {
					String monthStr = matcher.group(_monthGroupNumber);
					int month;
					try {
						month = Integer.parseInt(monthStr);
					} catch (NumberFormatException e) {
						Object monthNumber = _months.get(monthStr);
						if (monthNumber == null)
							throw new ParseException((new StringBuilder()).append("Invalid month: ")
									.append(monthStr).toString(), 0);
						month = ((Integer) monthNumber).intValue();
					}
					if (month < 1 || month > 12)
						throw new ParseException((new StringBuilder()).append("Invalid month: ")
								.append(month).toString(), 0);
					extract.set(Calendar.MONTH, month - 1);
				} else if (hasYear) {
					extract.set(Calendar.MONTH, 0);
				} else {
					Calendar now = getBasisCalendar(basisCalendar);
					if (_hoursGroupNumber < 0)
						now.set(Calendar.HOUR_OF_DAY, 0);
					if (_minutesGroupNumber < 0)
						now.set(Calendar.MINUTE, 0);
					if (_secondsGroupNumber < 0)
						now.set(Calendar.SECOND, 0);
					now.set(Calendar.MILLISECOND, 0);
					Calendar clonedExtract = (Calendar) extract.clone();
					boolean useClonedExtractForRollback = false;
					if (now.after(extract)) {
						extract.add(Calendar.MONTH, 1);
						useClonedExtractForRollback = true;
					}
					now.add(Calendar.DAY_OF_MONTH, _maxDaysAhead);
					if (now.before(extract))
						if (useClonedExtractForRollback)
							extract = clonedExtract;
						else
							extract.add(Calendar.MONTH, -1);
				}
				if (_yearGroupNumber >= 0) {
					String yearStr = matcher.group(_yearGroupNumber);
					try {
						int year = Integer.parseInt(yearStr);
						if (yearStr.length() <= 2) {
							year = 1900 + year;
							if (year < 1930)
								year += 100;
						}
						extract.set(Calendar.YEAR, year);
					} catch (NumberFormatException e) {
					}
				} else {
					Calendar now = getBasisCalendar(basisCalendar);
					if (_hoursGroupNumber < 0)
						now.set(Calendar.HOUR_OF_DAY, 0);
					if (_minutesGroupNumber < 0)
						now.set(Calendar.MINUTE, 0);
					if (_secondsGroupNumber < 0)
						now.set(Calendar.SECOND, 0);
					now.set(Calendar.MILLISECOND, 0);
					Calendar clonedExtract = (Calendar) extract.clone();
					boolean useClonedExtractForRollback = false;
					if (now.after(extract)) {
						extract.add(Calendar.YEAR, 1);
						useClonedExtractForRollback = true;
					}
					now.add(Calendar.MONTH, _maxMonthsAhead);
					if (now.before(extract))
						if (useClonedExtractForRollback)
							extract = clonedExtract;
						else
							extract.add(Calendar.YEAR, -1);
				}
			}
			if (_toTimeZone != null) {
				TimeZone fromTimeZone = null;
				if (_fromTimeZone != null && _overrideInputTimeZone)
					fromTimeZone = _fromTimeZone;
				else if (_timeZoneGroupNumber >= 0) {
					String timeZoneStr = matcher.group(_timeZoneGroupNumber);
					fromTimeZone = TimeZoneUtil.getTimeZone(timeZoneStr);
				}
				if (fromTimeZone == null)
					fromTimeZone = _fromTimeZone;
				if (fromTimeZone != null) {
					long time = extract.getTimeInMillis();
					int fromTimeZoneMillis = fromTimeZone.getOffset(time);
					int toTimeZoneMillis = _toTimeZone.getOffset(time);
					int delta = toTimeZoneMillis - fromTimeZoneMillis;
					extract.add(Calendar.MILLISECOND, delta);
				}
			}
			return extract;
		} else {
			throw new ParseException("No match found.", 0);
		}
	}

	private void compile() throws ParseException {
		StringBuffer pat = new StringBuffer();
		int charIndex = 0;
		int groupCount = 1;
		_dateGroupNumber = -1;
		_monthGroupNumber = -1;
		_yearGroupNumber = -1;
		_hoursGroupNumber = -1;
		_minutesGroupNumber = -1;
		_secondsGroupNumber = -1;
		_weekdayGroupNumber = -1;
		_amPmGroupNumber = -1;
		_timeZoneGroupNumber = -1;
		while (charIndex < _pattern.length()) {
			char ch = _pattern.charAt(charIndex++);
			int groupLength = 1;
			if (ch == 'E'
				|| ch == 'd'
				|| ch == 'M'
				|| ch == 'y'
				|| ch == 'h'
				|| ch == 'H'
				|| ch == 'm'
				|| ch == 's'
				|| ch == 'a')
				while (charIndex < _pattern.length() && _pattern.charAt(charIndex) == ch) {
					charIndex++;
					groupLength++;
				}
			if (ch == 'E') {
				_weekdayGroupNumber = groupCount++;
				pat.append(getWeekdayPattern(groupLength <= 3));
			} else if (ch == 'd') {
				_dateGroupNumber = groupCount++;
				pat.append("(\\d\\d?)");
			} else if (ch == 'M') {
				_monthGroupNumber = groupCount++;
				pat.append((new StringBuilder()).append("((?:").append(getMonthPattern())
						.append(")|\\d\\d?)").toString());
			} else if (ch == 'y') {
				_yearGroupNumber = groupCount++;
				if (groupLength == 4)
					pat.append("(\\d\\d\\d\\d)");
				else if (groupLength == 2)
					pat.append("(\\d\\d)");
				else
					pat.append("(\\d\\d?\\d?\\d?)");
			} else if (ch == 'h' || ch == 'H') {
				_hoursGroupNumber = groupCount++;
				pat.append("(\\d\\d?)");
			} else if (ch == 'm') {
				_minutesGroupNumber = groupCount++;
				pat.append("(\\d\\d?)");
			} else if (ch == 's') {
				_secondsGroupNumber = groupCount++;
				pat.append("(\\d\\d?)");
			} else if (ch == 'a') {
				_amPmGroupNumber = groupCount++;
				pat.append((new StringBuilder()).append("(").append(getAmPmPattern()).append(")").toString());
			} else if (ch == 'Z') {
				_timeZoneGroupNumber = groupCount++;
				pat.append("(.+)");
			} else if (ch == ' ')
				pat.append("\\s+");
			else if (ch == '*')
				pat.append(".*");
			else
				pat.append(escapeNonAlphaNumeric(ch));
		}
		try {
			_compiledPattern = Pattern.compile(pat.toString());
		} catch (PatternSyntaxException e) {
			throw e;
		}
	}

	private String getWeekdayPattern(boolean useShortNames) throws ParseException {
		StringBuffer sb;
		String weekdays[] = _symbols.getWeekdays();
		String shortWeekdays[] = _symbols.getShortWeekdays();
		String upperCasedWeekdayNames[] = new String[7];
		String upperCasedShortWeekdayNames[] = new String[7];
		upperCasedWeekdayNames[0] = weekdays[1].toUpperCase();
		upperCasedWeekdayNames[1] = weekdays[2].toUpperCase();
		upperCasedWeekdayNames[2] = weekdays[3].toUpperCase();
		upperCasedWeekdayNames[3] = weekdays[4].toUpperCase();
		upperCasedWeekdayNames[4] = weekdays[5].toUpperCase();
		upperCasedWeekdayNames[5] = weekdays[6].toUpperCase();
		upperCasedWeekdayNames[6] = weekdays[7].toUpperCase();
		upperCasedShortWeekdayNames[0] = shortWeekdays[1].toUpperCase();
		upperCasedShortWeekdayNames[1] = shortWeekdays[2].toUpperCase();
		upperCasedShortWeekdayNames[2] = shortWeekdays[3].toUpperCase();
		upperCasedShortWeekdayNames[3] = shortWeekdays[4].toUpperCase();
		upperCasedShortWeekdayNames[4] = shortWeekdays[5].toUpperCase();
		upperCasedShortWeekdayNames[5] = shortWeekdays[6].toUpperCase();
		upperCasedShortWeekdayNames[6] = shortWeekdays[7].toUpperCase();
		int minimumLength = getMinimumUniqueNameLength(upperCasedWeekdayNames);
		try {
			sb = new StringBuffer("(");
			for (int i = 0; i < 7; i++) {
				if (i > 0)
					sb.append("|");
				String dayPattern = getEscapedString(upperCasedWeekdayNames[i]);
				if (useShortNames)
					dayPattern = getPattern(getEscapedUpperCasedPrefixes(upperCasedWeekdayNames[i],
																			upperCasedShortWeekdayNames[i],
																			minimumLength));
				_compiledWeekdayPatterns[i] = Pattern.compile(dayPattern);
				sb.append(dayPattern);
			}

			sb.append(")");
			return sb.toString();
		} catch (PatternSyntaxException e) {
			throw new ParseException("Invalid weekdays.", 0);
		}

	}

	private String getMonthPattern() {
		List namesList = new ArrayList();
		for (int i = 0; i < _upperCasedMonthNames.length; i++)
			if (areNonEmpty(_upperCasedMonthNames[i], _upperCasedShortMonthNames[i]))
				namesList.addAll(getEscapedUpperCasedPrefixes(_upperCasedMonthNames[i],
																_upperCasedShortMonthNames[i],
																_minimumMonthNameLength));

		return getPattern(namesList);
	}

	private String getPattern(List stringList) {
		StringBuffer sb = new StringBuffer();
		Iterator iterator = stringList.iterator();
		do {
			if (!iterator.hasNext())
				break;
			sb.append((String) iterator.next());
			if (iterator.hasNext())
				sb.append("|");
		} while (true);
		return sb.toString();
	}

	private int getMinimumUniqueNameLength(String names[]) {
		Set prefixesFound = new HashSet();
		int length = 1;
		label0: do {
			if (prefixesFound.size() != names.length) {
				int i = 0;
				do {
					if (i >= names.length)
						continue label0;
					String string = getStringOrSubString(names[i], length);
					if (prefixesFound.contains(string)) {
						length++;
						prefixesFound.clear();
						continue label0;
					}
					prefixesFound.add(string);
					i++;
				} while (true);
			}
			return length;
		} while (true);
	}

	private List getEscapedUpperCasedPrefixes(String fullName, String shortName, int minimumUniqueLength) {
		List unescapedPrefixes = getUpperCasedPrefixes(fullName, shortName, minimumUniqueLength);
		List result = new ArrayList(unescapedPrefixes.size());
		for (Iterator iterator = unescapedPrefixes.iterator(); iterator.hasNext(); result
				.add(getEscapedString((String) iterator.next())))
			;
		return result;
	}

	private List getUpperCasedPrefixes(String fullName, String shortName, int minimumUniqueLength) {
		List prefixes = new ArrayList();
		prefixes.add(fullName);
		for (int i = fullName.length() - 1; i >= minimumUniqueLength; i--)
			prefixes.addAll(getDottedAndUndottedVersions(fullName.substring(0, i)));

		prefixes.addAll(getDottedAndUndottedVersions(shortName));
		Set alreadyContained = new HashSet();
		for (Iterator iterator = prefixes.iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			if (alreadyContained.contains(s))
				iterator.remove();
			else
				alreadyContained.add(s);
		}

		return prefixes;
	}

	private boolean isNonEmpty(String s) {
		return s != null && s.length() > 0;
	}

	private boolean areNonEmpty(String s1, String s2) {
		return isNonEmpty(s1) && isNonEmpty(s2);
	}

	private String getStringOrSubString(String s, int length) {
		return length >= s.length() ? s : s.substring(0, length);
	}

	private List getDottedAndUndottedVersions(String s) {
		return Arrays.asList(new String[] {
				s.endsWith(".") ? s : (new StringBuilder()).append(s).append(".").toString(),
				s.endsWith(".") ? s.substring(0, s.length() - 1) : s });
	}

	private String getEscapedString(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++)
			sb.append(escapeNonAlphaNumeric(s.charAt(i)));

		return sb.toString();
	}

	private String escapeNonAlphaNumeric(char ch) {
		if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9')
			return (new StringBuilder()).append("").append(ch).toString();
		else
			return (new StringBuilder()).append("\\").append(ch).toString();
	}

	private String getAmPmPattern() {
		StringBuffer result = new StringBuffer();
		String amStrings[] = getDottedAmPmPatterns(_amString);
		for (int i = 0; i < amStrings.length; i++)
			result.append((new StringBuilder()).append(amStrings[i]).append("|").toString());

		String pmStrings[] = getDottedAmPmPatterns(_pmString);
		for (int i = 0; i < pmStrings.length - 1; i++)
			result.append((new StringBuilder()).append(pmStrings[i]).append("|").toString());

		result.append(pmStrings[pmStrings.length - 1]);
		return result.toString();
	}

	private String[] getDottedAmPmStrings(String s) {
		return insertDots(s, ".");
	}

	private String[] getDottedAmPmPatterns(String s) {
		return insertDots(s, "\\.");
	}

	private String[] insertDots(String s, String dot) {
		String result[] = new String[4];
		result[0] = s;
		result[1] = (new StringBuilder()).append(s).append(dot).toString();
		StringBuffer dotted = new StringBuffer();
		int length = s.length();
		if (length > 0) {
			dotted.append(s.charAt(0));
			for (int i = 1; i < length; i++) {
				dotted.append(dot);
				dotted.append(s.charAt(i));
			}

			result[2] = dotted.toString();
			dotted.append(dot);
			result[3] = dotted.toString();
		} else {
			result[2] = "";
			result[3] = "";
		}
		return result;
	}

	private boolean isAmString(String s) {
		return matchesAmOrPmString(s, _amString);
	}

	private boolean isPmString(String s) {
		return matchesAmOrPmString(s, _pmString);
	}

	private boolean matchesAmOrPmString(String s, String amOrPmString) {
		String amOrPmStrings[] = getDottedAmPmStrings(amOrPmString);
		for (int i = 0; i < amOrPmStrings.length; i++)
			if (amOrPmStrings[i].equalsIgnoreCase(s))
				return true;

		return false;
	}

	private Calendar getBasisCalendar(Calendar basisCalendar) {
		return basisCalendar == null ? Calendar.getInstance() : (Calendar) basisCalendar.clone();
	}

	private static final int _weekdayValues[] = { 1, 2, 3, 4, 5, 6, 7 };
	private String _pattern;
	private int _maxMonthsAhead;
	private int _maxDaysAhead;
	private TimeZone _fromTimeZone;
	private TimeZone _toTimeZone;
	private boolean _overrideInputTimeZone;
	private Pattern _compiledPattern;
	private Pattern _compiledWeekdayPatterns[];
	private String _amString;
	private String _pmString;
	private Hashtable _months;
	private int _dateGroupNumber;
	private int _monthGroupNumber;
	private int _yearGroupNumber;
	private int _amPmGroupNumber;
	private int _hoursGroupNumber;
	private int _minutesGroupNumber;
	private int _secondsGroupNumber;
	private int _weekdayGroupNumber;
	private int _timeZoneGroupNumber;
	private DateFormatSymbols _symbols;
	private String _upperCasedMonthNames[];
	private String _upperCasedShortMonthNames[];
	private int _minimumMonthNameLength;
	
	public static void main(String[] args) throws ParseException{
		int zoneTime=13;
		//System.out.println("GMT"+zoneTime);
		DateExtractor extractor = new DateExtractor("zh_CN","yyyy-MM-dd",0,0, TimeZone.getTimeZone("GMT+8"), TimeZone.getTimeZone("GMT-13"),false);
		Calendar a = extractor.execute("14小时前");
		System.out.println(a.getTime().toLocaleString());
	}

}
