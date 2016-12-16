package com.cic.datacrawl.core.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.cic.datacrawl.core.ApplicationContext;
import com.cic.datacrawl.core.util.DateUtil;
import com.cic.datacrawl.core.util.FileUtils;

public class Config {
	private static final Logger LOGGER = Logger.getLogger(Config.class);
	private static Config instance;
	public static final String INSTALL_PATH = System.getProperty(Config.getUserDirPropertyName());

	public static Config getInstance() {
		if (instance == null)
			instance = new Config();
		return instance;
	}

	private Config() {
		try {
			init();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private static String logFilePath;

	public static boolean isLinux() {
		return System.getProperty("os.name").equalsIgnoreCase("linux");
	}

	public static String getLogFilePath() {
		if (logFilePath == null) {
			String time = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss.SSS").format(new Date(
					DateUtil.PROCESS_START_TIME));

			while (Config.INSTALL_PATH == null) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
			logFilePath = Config.INSTALL_PATH + File.separator + "logs" + File.separator + time;
		}
		return logFilePath;
	}

	public static void setJSFolder(String jsFolder) {
		System.setProperty("folder.jsfile.running", jsFolder);
	}

	public static String getJSFolder() {
		return System.getProperty("folder.jsfile.running");
	}

	public static String getUserDirPropertyName() {
		return "user.dir";
	}

	public static String getUserDir() {
		return System.getProperty(getUserDirPropertyName());
	}

	private Properties configProperty = new Properties();

	private void init() throws IOException {
		File f = new File(getConfigHome());
		if (!f.exists()) {
			f.mkdirs();
		}
		if (f.isFile()) {
			return;
		}
		// Application Configuration
		load(getConfigFile());

		// System Configuration
		Enumeration<Object> keys = System.getProperties().keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			configProperty.put(key, System.getProperty(key));
		}
	}

	public String getConfiguation(String configName) {
		if (configProperty.containsKey(configName)) {
			return configProperty.getProperty(configName);
		}
		return null;
	}

	protected void load(File file) throws IOException {
		Reader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			configProperty.load(reader);
		} catch (IOException e) {
			throw e;
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	public boolean isEditMode() {
		boolean ret = new Boolean(System.getProperty("mode.edit")).booleanValue();

		setConfiguation("mode.edit", "" + ret);
		return ret;
	}

	public void setWorkspaceHome(String workspaceHome) {
		setConfiguation(ConfigConstant.WORKSPACE_HOME, workspaceHome);
	}
	
	public void setCurrentWorkspaceHome(String workspaceHome) {
		setConfiguation(ConfigConstant.CURRENT_WORKSPACE_HOME, workspaceHome);
		System.setProperty("user.dir", workspaceHome);
	}
	
	public String getCurrentWorkspace() {
		if (!contains(ConfigConstant.CURRENT_WORKSPACE_HOME)) {
			return FileUtils.buildAbsolutelyPath(getUserDir(), "Script");
		}
		return getConfiguation(ConfigConstant.CURRENT_WORKSPACE_HOME);
	}

	public void setCurrentWindow(String url) {
		if (url == null || url.length() == 0) {
			configProperty.remove(ConfigConstant.CURRENT_OPEN_FILE);
		}
		setConfiguation(ConfigConstant.CURRENT_OPEN_FILE, url);
	}

	public String getCurrentWindow() {
		if (configProperty.contains(ConfigConstant.CURRENT_OPEN_FILE)) {
			return getConfiguation(ConfigConstant.CURRENT_OPEN_FILE);
		}
		return null;
	}

	private void setConfiguation(String key, String value) {
		if (key != null && key.trim().length() > 0 && value != null && value.trim().length() > 0) {

			configProperty.put(key, value);
		}
	}

	public void save() throws IOException {
		save(getConfigFile());
	}

	protected void save(File file) throws IOException {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, false));
			configProperty.store(writer, "");
		} catch (IOException e) {
			throw e;
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	public Properties getAllConfiguations() {
		return configProperty;
	}

	public boolean contains(String configName) {
		return configProperty.containsKey(configName);
	}

	public String getOSName() {
		return System.getProperty("os.name");
	}

	public String getUserHome() {
		return System.getProperty("user.home");
	}

	public String getWorkspace() {
		if (!contains(ConfigConstant.WORKSPACE_HOME)) {
			return FileUtils.buildAbsolutelyPath(getUserDir(), "Script");
		}
		return getConfiguation(ConfigConstant.WORKSPACE_HOME);
	}

	public String[] getDefaultOpenFile() {
		if (!contains(ConfigConstant.DEFAULT_OPEN_FILES)) {
			return null;
		}
		String filenameStr = getConfiguation(ConfigConstant.DEFAULT_OPEN_FILES);
		if (filenameStr.trim().length() == 0)
			return null;
		return filenameStr.split(File.pathSeparator);
	}

	public void putDefaultOpenFile(String[] filenames) {
		if (filenames == null || filenames.length == 0) {
			configProperty.remove(ConfigConstant.DEFAULT_OPEN_FILES);
		}
		StringBuffer filename = new StringBuffer();
		if (filenames != null) {
			for (int i = 0; i < filenames.length; ++i) {
				if (filenames[i] != null && filenames[i].trim().length() > 0) {
					if (i > 0)
						filename.append(File.pathSeparator);
					filename.append(filenames[i]);
				}
			}
		}
		configProperty.put(ConfigConstant.DEFAULT_OPEN_FILES, filename.toString());
	}

	public String getConfigHome() {
		return getUserHome() + File.separator + ".script";
	}

	public String getConfigFileName() {
		return getConfigHome() + File.separator + "config.properties";
	}

	private File getConfigFile() {
		File f = new File(getConfigFileName());
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
			}
		}
		return f;
	}

	public String getClickColor() {
		String colorValueString = "" + Integer.valueOf("00FF00", 16).intValue();
		if (contains(ConfigConstant.CLICK_COLOR)) {
			colorValueString = getConfiguation(ConfigConstant.CLICK_COLOR);
		}
		return colorValueString;
	}

	public String getBaseColor() {
		String colorValueString = "" + Integer.valueOf("FF00FF", 16).intValue();
		if (contains(ConfigConstant.BASE_COLOR)) {
			colorValueString = getConfiguation(ConfigConstant.BASE_COLOR);
		}
		return colorValueString;
	}

	public int getIntClickColor() {
		return Integer.parseInt(getClickColor());
	}

	public int getIntBaseColor() {
		return Integer.parseInt(getBaseColor());
	}

	public void setClickColor(String color) {
		if (color != null) {
			configProperty.remove(ConfigConstant.CLICK_COLOR);
		}

		configProperty.put(ConfigConstant.CLICK_COLOR, color);
	}

	public void setBaseColor(String color) {
		if (color != null) {
			configProperty.remove(ConfigConstant.BASE_COLOR);
		}

		configProperty.put(ConfigConstant.BASE_COLOR, color);
	}

	public String getOutputPath() {
		String outputPath = null;
		try {
			outputPath = ((SpringConfiguration) ApplicationContext.getInstance().getBean("config"))
					.getOutputPath();
		} catch (Exception e) {
			LOGGER.error("Can not get outputPath, and set the outputPath to Defaule: xmloutput",e);
			outputPath = "xmloutput" + File.separator;
		}
		return outputPath;
	}
}
