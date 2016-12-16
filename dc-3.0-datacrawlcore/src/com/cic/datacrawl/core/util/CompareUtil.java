package com.cic.datacrawl.core.util;

public class CompareUtil {
	public static int compareObject(Byte o1, byte b) {
		return o1.compareTo(new Byte(b));
	}

	public static int compareObject(Byte o1, Byte o2) {
		return o1.compareTo(o2);
	}

	public static int compareObject(Character o1, char c) {
		return o1.compareTo(new Character(c));
	}

	public static int compareObject(Number o1, Number o2) {
		return (int) (o1.doubleValue() - o2.doubleValue());
	}

	public static int compareObject(Character o1, Character o2) {
		return o1.compareTo(o2);
	}

	public static int compareObject(Double o1, double d) {
		return o1.compareTo(new Double(d));
	}

	public static int compareObject(Double o1, Double o2) {
		return o1.compareTo(o2);
	}

	public static int compareObject(Double o1, float f) {
		return o1.compareTo(new Double(f));
	}

	public static int compareObject(Float o1, float f) {
		return o1.compareTo(new Float(f));
	}

	public static int compareObject(Float o1, Float o2) {
		return o1.compareTo(o2);
	}

	public static int compareObject(Integer o1, int i) {
		return o1.compareTo(new Integer(i));
	}

	public static int compareObject(Integer o1, Integer o2) {
		return o1.compareTo(o2);
	}

	public static int compareObject(Integer o1, short s) {
		return o1.compareTo(new Integer(s));
	}

	public static int compareObject(Long o1, int i) {
		return o1.compareTo(new Long(i));
	}

	public static int compareObject(Long o1, long l) {
		return o1.compareTo(new Long(l));
	}

	public static int compareObject(Long o1, Long o2) {
		return o1.compareTo(o2);
	}

	public static int compareObject(Long o1, short s) {
		return o1.compareTo(new Long(s));
	}

	public static int compareObject(Object o1, Object o2) {
		if (o1 instanceof Number && o2 instanceof Number) {
			return compareObject((Number) o1, (Number) o2);
		} else if (o1 instanceof Character && o2 instanceof Character) {
			return compareObject((Character) o1, (Character) o2);
		}

		return o1.toString().compareTo(o2.toString());
	}

	public static int compareObject(Short o1, short s) {
		return o1.compareTo(new Short(s));
	}

	public static int compareObject(Short o1, Short o2) {
		return o1.compareTo(o2);
	}

	public static int compareObject(String o1, byte[] bytes) {
		return o1.compareTo(new String(bytes));
	}

	public static int compareObject(String o1, String o2) {
		return o1.compareTo(o2);
	}
}
