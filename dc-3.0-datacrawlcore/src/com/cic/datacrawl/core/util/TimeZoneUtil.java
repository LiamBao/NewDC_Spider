package com.cic.datacrawl.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public final class TimeZoneUtil {
	private static class TimeZoneDisplayNameComparator implements Comparator {

		public int compare(TimeZone tz1, TimeZone tz2) {
			return tz1.getDisplayName().compareTo(tz2.getDisplayName());
		}

		public int compare(Object x0, Object x1) {
			return compare((TimeZone) x0, (TimeZone) x1);
		}

		private TimeZoneDisplayNameComparator() {
		}

	}

	private static void addToTimeZoneMap(String name, TimeZone tz) {
		name = name.toUpperCase();
		if (!_timeZoneMap.containsKey(name))
			_timeZoneMap.put(name, tz);
	}

	public static TimeZone getTimeZone(String name) {
		return (TimeZone) _timeZoneMap.get(name.toUpperCase());
	}

	public static List getAllRecognizedTimeZoneNames() {
		List result = new ArrayList(_timeZoneMap.keySet());
		Collections.sort(result);
		return result;
	}

	public static List getAllTimeZoneDisplayNames() {
		List result = new ArrayList();
		TimeZone timeZone;
		for (Iterator i$ = getAllTimeZonesWithDifferentDisplayNames().iterator(); i$.hasNext(); result
				.add(timeZone.getDisplayName()))
			timeZone = (TimeZone) i$.next();

		return result;
	}

	public static List getAllTimeZonesWithDifferentDisplayNames() {
		List result = new ArrayList();
		Set usedNames = new HashSet();
		String defaultTimeZoneDisplayName = DEFAULT_TIME_ZONE.getDisplayName(Locale.US);
		Iterator i$ = _timeZoneMap.values().iterator();
		do {
			if (!i$.hasNext())
				break;
			TimeZone timeZone = (TimeZone) i$.next();
			String displayName = timeZone.getDisplayName(Locale.US);
			if (!usedNames.contains(displayName)
				&& (!displayName.equals(defaultTimeZoneDisplayName) || timeZone.equals(DEFAULT_TIME_ZONE))) {
				result.add(timeZone);
				usedNames.add(displayName);
			}
		} while (true);
		Collections.sort(result, new TimeZoneDisplayNameComparator());
		return result;
	}

	public static List getAllTimeZones() {
		List result = new ArrayList(_timeZoneMap.values());
		Collections.sort(result, new TimeZoneDisplayNameComparator());
		return result;
	}

	public static TimeZone getCurrentTimeZone() {
		return (new GregorianCalendar()).getTimeZone();
	}

	private TimeZoneUtil() {
	}

	public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("America/Los_Angeles");
	private static final Map _timeZoneMap = new HashMap();

	static {
		String ids[] = TimeZone.getAvailableIDs();
		String arr$[] = ids;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++) {
			String id = arr$[i$];
			TimeZone tz = TimeZone.getTimeZone(id);
			addToTimeZoneMap(id, tz);
			addToTimeZoneMap(tz.getDisplayName(false, 0), tz);
			addToTimeZoneMap(tz.getDisplayName(true, 0), tz);
			addToTimeZoneMap(tz.getDisplayName(false, 1), tz);
			addToTimeZoneMap(tz.getDisplayName(true, 1), tz);
		}

	}
}