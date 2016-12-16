package com.cic.datacrawl.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.entity.SaveEntityToCollectServer;

public class RecordCollectRunnerTest {
	
	public static void main(String[] args) {
		String path = Config.INSTALL_PATH + File.separator + "config" + File.separator + "beans";
		ApplicationContext.initialiaze(path, true);
		//initFQConf();
		SaveEntityToCollectServer collectServer = (SaveEntityToCollectServer)(ApplicationContext.getInstance().getBean("saveManager"));
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> one = new ArrayList<String>();
		List<String> two = new ArrayList<String>();
		List<String> three = new ArrayList<String>();
		
		
		for(int i =0;i < 6;i++){
			String str2 = "因为double型小数部分会\n在从二进制变十进制的类型变换中产生误差，" +
					"所以x2会变成-3.80000000000003，如果要使输出四舍五入 保留两位小数，" +
					"可以用String的format函数，方法\n如下：System.out.println(String." +
					"format(%.2f, x1));System.out.printlnprintln5555(String.format(%.2f, x2));";
			String str = "因为double型小数部分会\n在从'二进'制\t变十\\进制的类***/*//[]\\/|/\\{}.型变换中产生误差，" +
					"所以x2会变成-3.80000sdg;'/,.，。‘；？、；’‘；’。、，.//,.000" +
					"<>《》《》？、、、//;::；：：“”“”“”\"\"000003，如果要使输出@#$%^&*()_+!~`四舍五///入 保留两位小数，" +
					"可以用String的format函数，方法\n如下：System.out.println(String." +
					"format(%.2f, x1));System.out.printlnprintln5555(String.format(%.2f, x2));";
			System.out.println(str.length());
			StringBuffer sb = new StringBuffer(str);
			for(int j = 0 ; j <4; j++){
				sb = sb.append(sb);
			}
			str = sb.toString();
			System.out.println(str.length());
			
			one.add(i+ str2);
			two.add(i + str);
			three.add(i+ str);
		}
		
		
		
		map.put("AAA_CCC_DDD", one);
		map.put("BBB_CCC_DDD", two);
		map.put("CCC_CCC_DDD", three);
		
		collectServer.setEntityJsons(map);
		
		
		
		try {
			collectServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("发送失败哦！");
		}
		
	}
}
