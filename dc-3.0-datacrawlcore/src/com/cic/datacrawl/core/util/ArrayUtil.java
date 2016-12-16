package com.cic.datacrawl.core.util;

import java.sql.Timestamp;

/**
 * @author Page 5
 */
public class ArrayUtil {

	public static String[] mergeStringArray(String[] array1, String[] array2) {
		if (array1 == null)
			array1 = new String[0];
		if (array2 == null)
			array2 = new String[0];
		if (array1.length == 0 && array2.length == 0) {
			return new String[0];
		} else if (array1.length == 0) {
			return array2;
		} else if (array2.length == 0) {
			return array1;
		}
		String[] retArray = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, retArray, 0, array1.length);
		System.arraycopy(array2, 0, retArray, array1.length, array2.length);
		return retArray;
	}

	public static int[] mergeStringArray(int[] array1, int[] array2) {
		if (array1 == null)
			array1 = new int[0];
		if (array2 == null)
			array2 = new int[0];
		if (array1.length == 0 && array2.length == 0) {
			return new int[0];
		} else if (array1.length == 0) {
			return array2;
		} else if (array2.length == 0) {
			return array1;
		}
		int[] retArray = new int[array1.length + array2.length];
		System.arraycopy(array1, 0, retArray, 0, array1.length);
		System.arraycopy(array2, 0, retArray, array1.length, array2.length);
		return retArray;
	}

	public static boolean isArray(Object o) {
		if (o == null)
			return false;
		return isObjectArray(o) || isLightObjectArray(o);
	}

	public static boolean isObjectArray(Object o) {
		if (o == null)
			return false;
		return (o instanceof Object[]);
	}

	public static boolean isLightObjectArray(Object o) {
		if (o == null)
			return false;
		return (o instanceof long[])
				|| (o instanceof int[])
				|| (o instanceof short[])
				|| (o instanceof double[])
				|| (o instanceof float[])
				|| (o instanceof char[])
				|| (o instanceof byte[])
				|| (o instanceof boolean[]);
	}

	public static void main(String[] args) {
		System.out.println(isObjectArray(new String[0]));

		System.out.println(isLightObjectArray(new short[0]));

		System.out.println(isLightObjectArray(new double[0]));

		System.out.println(isObjectArray(new Number[0]));

		System.out.println(isObjectArray(new Timestamp[0]));
	}

	public static String toString(Object[] o) {
		if (o == null)
			return null;
		StringBuilder sb = new StringBuilder(o.toString());
		sb.append("{");
		for (int i = 0; i < o.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(o[i].toString());
		}
		sb.append("}");
		return sb.toString();
	}

	public static String toString(short[] o) {
		if (o == null)
			return null;
		StringBuilder sb = new StringBuilder(o.toString());
		sb.append("{");
		for (int i = 0; i < o.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(o[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	public static String toString(int[] o) {
		if (o == null)
			return null;
		StringBuilder sb = new StringBuilder(o.toString());
		sb.append("{");
		for (int i = 0; i < o.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(o[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	public static String toString(long[] o) {
		if (o == null)
			return null;
		StringBuilder sb = new StringBuilder(o.toString());
		sb.append("{");
		for (int i = 0; i < o.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(o[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	public static String toString(float[] o) {
		if (o == null)
			return null;
		StringBuilder sb = new StringBuilder(o.toString());
		sb.append("{");
		for (int i = 0; i < o.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(o[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	public static String toString(double[] o) {
		if (o == null)
			return null;
		StringBuilder sb = new StringBuilder(o.toString());
		sb.append("{");
		for (int i = 0; i < o.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(o[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	public static String toString(char[] o) {
		if (o == null)
			return null;
		StringBuilder sb = new StringBuilder(o.toString());
		sb.append("{");
		for (int i = 0; i < o.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(o[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	public static String toString(byte[] o) {
		if (o == null)
			return null;
		StringBuilder sb = new StringBuilder(o.toString());
		sb.append("{");
		for (int i = 0; i < o.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(o[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	public static long[] parseToLongArray(String[] numbersStr) {

		if (numbersStr == null) {
			return new long[0];
		}
		int length = numbersStr.length;
		if (length == 0) {
			return new long[0];
		}
		long[] ret = new long[length];

		for (int i = 0; i < length; ++i) {
			try {
				ret[i] = Long.parseLong(numbersStr[i]);
			} catch (Exception e) {
			}
		}

		return ret;
	}

	public static String toString(boolean[] o) {
		if (o == null)
			return null;
		StringBuilder sb = new StringBuilder(o.toString());
		sb.append("{");
		for (int i = 0; i < o.length; ++i) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(o[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	public static boolean isEmpty(Object[] objects) {
		if (objects == null || objects.length == 0)
			return true;

		for (int i = 0; i < objects.length; ++i) {
			if (objects[i] != null)
				return false;
		}

		return true;
	}
	
	public static boolean isNotEmpty(Object[] objects) {
		return !isEmpty(objects);
	}

	public static short[] parseToShortArray(Number[] numbers) {
		if (numbers == null) {
			return new short[0];
		}
		int length = numbers.length;
		if (length == 0) {
			return new short[0];
		}
		short[] ret = new short[length];

		for (int i = 0; i < length; ++i) {
			ret[i] = numbers[i].shortValue();
		}

		return ret;
	}

	public static int[] parseToIntArray(Number[] numbers) {
		if (numbers == null) {
			return new int[0];
		}
		int length = numbers.length;
		if (length == 0) {
			return new int[0];
		}
		int[] ret = new int[length];

		for (int i = 0; i < length; ++i) {
			ret[i] = numbers[i].intValue();
		}

		return ret;
	}

	public static long[] parseToLongArray(Number[] numbers) {

		if (numbers == null) {
			return new long[0];
		}
		int length = numbers.length;
		if (length == 0) {
			return new long[0];
		}
		long[] ret = new long[length];

		for (int i = 0; i < length; ++i) {
			ret[i] = numbers[i].longValue();
		}

		return ret;
	}

	public static float[] parseToFloatArray(Number[] numbers) {

		if (numbers == null) {
			return new float[0];
		}
		int length = numbers.length;
		if (length == 0) {
			return new float[0];
		}
		float[] ret = new float[length];

		for (int i = 0; i < length; ++i) {
			ret[i] = numbers[i].floatValue();
		}

		return ret;
	}

	public static double[] parseToDoubleArray(Number[] numbers) {

		if (numbers == null) {
			return new double[0];
		}
		int length = numbers.length;
		if (length == 0) {
			return new double[0];
		}
		double[] ret = new double[length];

		for (int i = 0; i < length; ++i) {
			ret[i] = numbers[i].doubleValue();
		}

		return ret;
	}

	public static boolean content(byte[] array, byte num) {
		if (array == null) {
			return false;
		}
		int length = array.length;
		if (length == 0) {
			return false;
		}

		for (int i = 0; i < length; ++i) {
			if (array[i] == num)
				return true;
		}

		return false;
	}

	public static boolean content(short[] array, short num) {
		if (array == null) {
			return false;
		}
		int length = array.length;
		if (length == 0) {
			return false;
		}

		for (int i = 0; i < length; ++i) {
			if (array[i] == num)
				return true;
		}

		return false;
	}

	public static boolean content(int[] array, int num) {
		if (array == null) {
			return false;
		}
		int length = array.length;
		if (length == 0) {
			return false;
		}

		for (int i = 0; i < length; ++i) {
			if (array[i] == num)
				return true;
		}

		return false;
	}

	public static boolean content(long[] array, long num) {
		if (array == null) {
			return false;
		}
		int length = array.length;
		if (length == 0) {
			return false;
		}

		for (int i = 0; i < length; ++i) {
			if (array[i] == num)
				return true;
		}

		return false;
	}

	public static boolean content(float[] array, float num) {
		if (array == null) {
			return false;
		}
		int length = array.length;
		if (length == 0) {
			return false;
		}

		for (int i = 0; i < length; ++i) {
			if (array[i] == num)
				return true;
		}

		return false;
	}

	public static boolean content(double[] array, double num) {
		if (array == null) {
			return false;
		}
		int length = array.length;
		if (length == 0) {
			return false;
		}

		for (int i = 0; i < length; ++i) {
			if (array[i] == num)
				return true;
		}

		return false;
	}

	public static boolean content(char[] array, char num) {
		if (array == null) {
			return false;
		}
		int length = array.length;
		if (length == 0) {
			return false;
		}

		for (int i = 0; i < length; ++i) {
			if (array[i] == num)
				return true;
		}

		return false;
	}

	public static boolean content(Object[] array, Object object) {

		if (array == null) {
			return false;
		}
		int length = array.length;
		if (length == 0) {
			return false;
		}

		for (int i = 0; i < length; ++i) {
			if (object.equals(array[i]))
				return true;
		}

		return false;
	}

	// public static boolean content(Number[] numbers, Number number) {
	//
	// if (numbers == null) {
	// return false;
	// }
	// int length = numbers.length;
	// if (length == 0) {
	// return false;
	// }
	//
	// for (int i = 0; i < length; ++i) {
	// if (number.doubleValue() == numbers[i].doubleValue())
	// return true;
	// }
	//
	// return false;
	// }

	public static Short[] parseToShortObjectArray(Number[] numbers) {

		if (numbers == null) {
			return new Short[0];
		}
		if (numbers instanceof Short[]) {
			return (Short[]) numbers;
		}
		int length = numbers.length;
		if (length == 0) {
			return new Short[0];
		}
		try {
			return (Short[]) numbers;
		} catch (Exception e) {
			Short[] ret = new Short[length];

			for (int i = 0; i < length; ++i) {
				ret[i] = numbers[i].shortValue();
			}

			return ret;
		}
	}

	public static Integer[] parseToIntObjectArray(Number[] numbers) {

		if (numbers == null) {
			return new Integer[0];
		}
		if (numbers instanceof Integer[]) {
			return (Integer[]) numbers;
		}
		int length = numbers.length;
		if (length == 0) {
			return new Integer[0];
		}
		try {
			return (Integer[]) numbers;
		} catch (Exception e) {
			Integer[] ret = new Integer[length];

			for (int i = 0; i < length; ++i) {
				ret[i] = numbers[i].intValue();
			}

			return ret;
		}

	}

	public static Long[] parseToLongObjectArray(Number[] numbers) {

		if (numbers == null) {
			return new Long[0];
		}
		if (numbers instanceof Long[]) {
			return (Long[]) numbers;
		}
		int length = numbers.length;
		if (length == 0) {
			return new Long[0];
		}
		try {
			return (Long[]) numbers;
		} catch (Exception e) {
			Long[] ret = new Long[length];

			for (int i = 0; i < length; ++i) {
				ret[i] = numbers[i].longValue();
			}

			return ret;
		}
	}

	public static Float[] parseToFloatObjectArray(Number[] numbers) {

		if (numbers == null) {
			return new Float[0];
		}
		if (numbers instanceof Float[]) {
			return (Float[]) numbers;
		}
		int length = numbers.length;
		if (length == 0) {
			return new Float[0];
		}
		try {
			return (Float[]) numbers;
		} catch (Exception e) {
			Float[] ret = new Float[length];

			for (int i = 0; i < length; ++i) {
				ret[i] = numbers[i].floatValue();
			}

			return ret;
		}
	}

	public static Double[] parseToDoubleObjectArray(Number[] numbers) {

		if (numbers == null) {
			return new Double[0];
		}
		if (numbers instanceof Double[]) {
			return (Double[]) numbers;
		}
		int length = numbers.length;
		if (length == 0) {
			return new Double[0];
		}
		try {
			return (Double[]) numbers;
		} catch (Exception e) {
			Double[] ret = new Double[length];

			for (int i = 0; i < length; ++i) {
				ret[i] = numbers[i].doubleValue();
			}

			return ret;
		}
	}
}
