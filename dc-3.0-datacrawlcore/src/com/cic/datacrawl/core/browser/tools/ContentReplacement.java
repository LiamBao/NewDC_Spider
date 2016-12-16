package com.cic.datacrawl.core.browser.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该类用来将BROWSER内容的源码中符合regString表达式的替换为replaceTo
 * 
 * @author rex.wu
 * 
 */
public class ContentReplacement {
	private String regString;
	private String name;

	/**
	 * @return the regString
	 */
	public String getRegString() {
		return regString;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the replaceTo
	 */
	public String getReplaceTo() {
		return replaceTo;
	}

	private Pattern pattern;
	private String replaceTo;

	public ContentReplacement(String regString, String replaceTo) {
		this.name = regString;
		this.regString = regString;
		this.replaceTo = replaceTo == null ? "" : replaceTo;
		if (regString != null)
			try {
				pattern = Pattern.compile(regString, Pattern.DOTALL);
			} catch (Throwable t) {
			}
	}

	public String doFilter(String content) {
		if (pattern == null)
			return content;

		Matcher matcher = pattern.matcher(content);
		String ret = matcher.replaceAll(replaceTo);
		return ret;
	}

	public static void main(String[] args) {
		String src = "abcd1234efgh5678ijkl3534mno345p4qr573457st4567457uv456745wxyz\nAB87C3456DE24F2GH2345I34J56K5L45M7N457OP767Q45R74S567T54U756V7W56X745YZ";

		ContentReplacement f = new ContentReplacement("\\d+", "");
		// System.out.println(f.doFilter(src));

		// src =
		// "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n\n<html>\n<head>\n	<title>Untitled</title>\n</head>\n\n<body>\n\n<table>\n	<tr>\n		<td>2</td>\n		<td>3</td>\n	</tr>\n	<tr>\n		<td>4</td>\n		<td>5</td>\n	</tr>\n	<tr>\n		<td>6</td>\n		<td>7</td>\n	</tr>\n</table>\n\n\n</body>\n</html>\n";
		// f = new ContentReplacement("noData", "<tr>.*</tr>", "");
		// System.out.println(f.doFilter(src));

		src = "                      <BR/>\n　　“小开”比“公子”多了点俗气，也多了几分诙谐；很有种不以为然的海派作风，什么稀奇？不过老子多几个铜钿，再神气，也得个“小”字。\n                      <FONT STYLE=\"font-size: 0pt;\">\nLM0YU&amp;]M\n</FONT>\n                      <BR/>\n                      <FONT STYLE=\"font-size: 0pt;\">\nzWg\\dMQ0X\n</FONT>\n                      <BR/>\n　　“小开”很百搭，不管酱园店小开还是百乐门小开，搭上去都很顺耳，换个词，酱园店公子，南货店少爷，百乐门少东家……都没有“小开”传神，口语化。\n                      <FONT STYLE=\"font-size: 0pt;\">\n*={KrL\n</FONT>\n                      <BR/>\n                      <FONT STYLE=\"font-size: 0pt;\">\n\\D _B(\n</FONT>\n                      <BR/>\n　　“小开”十分神髓地描绘出这样一簇\n                      <SPAN ONCLICK=\"showTip(event, 'http://www.longdang.com/bbs/tag.php?name=%C9%CF%BA%A3%C4%D0%C8%CB');\" HREF=\"###\">\n上海男人\n</SPAN>\n：一般没有自己独立打理的一爿生意或赖以作主要生活来源的专业，只恃着老爸或老家的财势，却一样过得鲜亮风光；因为是小开，凡事不知轻重，不分尊卑，喜招摇过市……因为有的是时间和铜钿，小开棋琴诗画，跳舞桥牌沙蟹麻将网球玩票，都知一点，又因为天生懒散，大都是三脚猫。\n                      <FONT STYLE=\"font-size: 0pt;\">\nukoKG$e28\n</FONT>\n                      <BR/>\n                      <FONT STYLE=\"font-size: 0pt;\">\n{Z{h,@ZWN\n</FONT>\n                      <BR/>\n　　在旧上海，小开是一众小家碧玉的东床快婿，是职业女性婚姻中的恶梦。\n                      <FONT STYLE=\"font-size: 0pt;\">\nip:yhVrs\n</FONT>\n                      <BR/>";
		f = new ContentReplacement("(?i)<font STYLE=\"font-size: 0pt;\">.*</font>", "");
		System.out.println(f.doFilter(src));
	}
}
