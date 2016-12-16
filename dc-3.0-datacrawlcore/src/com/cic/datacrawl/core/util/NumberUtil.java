package com.cic.datacrawl.core.util;

import java.util.ArrayList;

public class NumberUtil {
	private static final String[][] CHINESE_NUMBER = {
			{ "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "廿", "卅", "百", "千", "万" },
			{ "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾", null, null, "佰", "仟", "萬" },
			{ null, null, "貳", null, null, null, "陸" } /*
														 * , { null, null, "两"
														 * }, { null, null, "兩"
														 * }
														 */};

	private static final long[] NUMBER = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 100, 1000, 10000,
			100000000 };

	/**
	 * 仅支持到千万
	 * 
	 * @param str
	 * @return
	 */
	public static long convertToNumber(String str) {
		if (str == null || str.trim().length() == 0) {
			return 0;
		}
		long ret = 0;

		try {
			ret = Long.parseLong(str);
		} catch (Exception e) {
			ArrayList<Long> numberList = new ArrayList<Long>();
			ArrayList<String> chineseList = new ArrayList<String>();

			for (int i = 0; i < str.length(); ++i) {
				if (i == str.length() - 1) {
					chineseList.add(str.substring(i));
				} else {
					chineseList.add(str.substring(i, i + 1));
				}
			}
			boolean needCalc = false;
			for (int i = chineseList.size() - 1; i >= 0; --i) {
				String singleNumberStr = chineseList.get(i);
				for (int j = 0; j < CHINESE_NUMBER.length; ++j) {
					for (int k = 0; k < CHINESE_NUMBER[j].length; ++k) {
						if (singleNumberStr.equals(CHINESE_NUMBER[j][k])) {
							long number = NUMBER[k];
							if (!needCalc && number > 9) {
								needCalc = true;
							}
							numberList.add(new Long(number));
						}
					}
				}
			}

			if (needCalc) {
				for (int i = numberList.size() - 1; i >= 0; --i) {
					long currentNumber = numberList.get(i).longValue();
					if (currentNumber > 9) {
						long num = ret;
						if (i == numberList.size() - 1
							|| (numberList.get(i + 1).longValue() > 9 && numberList.get(i + 1).longValue() < 100)) {
							num = 1;
						}
						ret = ret + num * currentNumber;
					} else {
						int j = i - 1;
						long module1 = 1;
						if (j >= 0) {
							long l = numberList.get(j).longValue();
							if (l > 9) {
								module1 = l;
								--i;
							}
						}
						ret = ret + (currentNumber * module1);
					}
				}
			} else {
				StringBuilder sb = new StringBuilder();
				for (int i = numberList.size() - 1; i >= 0; --i) {
					sb.append(numberList.get(i).longValue());
				}
				ret = Long.parseLong(sb.toString());
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		String[] str = {  "五百十五",/* "十五", "廿二", "廿", "卅二", "一九零八柒四贰五玖", "一千九佰零八", */"一千九佰万", "一千九佰万零八", /*
																										 * "十二",
																										 * "廿二"
																										 * ,
																										 * "廿"
																										 * ,
																										 * "卅二"
																										 * ,
																										 * "三十貳"
																										 * ,
																										 * "二十"
																										 * ,
																										 * "壹貳叁肆伍陸柒捌玖"
																										 * ,
																										 * "三千万万"
																										 */};
		for (int i = 0; i < str.length; ++i) {
			System.out.println(convertToNumber(str[i]));
		}
	}

}
