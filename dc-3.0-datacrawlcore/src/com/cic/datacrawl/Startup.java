package com.cic.datacrawl;

import java.io.File;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.initialize.InitializerRegister;

public class Startup {
	private static final Logger logger = Logger.getLogger(Startup.class);

	/**
	 * @param args
	 */
	public static void main(String[] args){
		
		try {
			

		String path = null;
		boolean reflash = true;
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("-h")) {
				System.out.println("Command Format: "
						+ "%JAVA_HOME%\\BIN\\JAVA -jar homepageCatcher.jar "
						+ "[-d config path]");
			}

			int pathIndex = ArrayUtils.indexOf(args, "-d");

			path = args[pathIndex + 1];
			try {
				File f = new File(path);
				if (!f.exists()) {
					System.err.println("Invalid path of configuration.");
				}
			} catch (Throwable e) {
				System.err.println("Invalid path of configuration.");
			}
			// int reflashIndex = ArrayUtils.indexOf(args, "-r");
			//
			// reflash = new Boolean(args[reflashIndex + 1]).booleanValue();
		}
		if (path == null || path.trim().length() == 0)
			path = System.getProperty("user.dir") + "\\config\\beans";

		// 启动IOC容器
		// 启动配置管理程序
		// 装载默认配置文件
		ApplicationContext.initialiaze(path, reflash);
		System.setProperty(ApplicationContext.CONFIG_PATH, path);

		// 启动初始化
		InitializerRegister.getInstance().execute();
		
		
		
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Startup is error", e);
	
		}
		
		
		
		// PropertyConfigurator.configure(System.getProperty("user.dir")+"\\config\\log4j\\manager\\log4j.properties");

		// 启动RMI服务
		// try {
		// RmiConfiguration rmiConfiguration = (RmiConfiguration)
		// ApplicationContext
		// .getInstance().getBean("rmiConfiguration");
		//
		// RMIImpl remoter = new RMIImpl();
		// Registry registry = LocateRegistry.createRegistry(rmiConfiguration
		// .getPort());
		// registry.rebind(rmiConfiguration.getBindpath(), remoter);
		//
		// String tsIP = InetAddress.getLocalHost().getHostAddress();
		// logger.info("SampleServer started. AT rmi://" + tsIP + ":"
		// + rmiConfiguration.getPort() + "/"
		// + rmiConfiguration.getBindpath());
		// } catch (Exception e) {
		// logger.error("SampleServer start error!", e);
		//
		// }

	}
}
