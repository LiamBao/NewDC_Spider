package com.cic.datacrawl.crawler;

import java.util.Random;

public class UserAgentUtil {
	private static final String[] userAgentArray = new String[]{
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0)", 
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20070309 Firefox/2.0.0.3",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20070803 Firefox/1.5.0.12",
			"Opera/9.27 (Windows NT 5.2; U; zh-cn)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Version/3.1 Safari/525.13",
			"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.72 Safari/537.36",
			"Mozilla/5.0 (Windows NT 5.1; rv:22.0) Gecko/20100101 Firefox/22.0"};

	public static String getUserAgent() {
		Random random = new Random();
		int index = random.nextInt(userAgentArray.length);
		return userAgentArray[index];
	}
}
