package com.cic.datacrawl.core;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ApplicationContext {
	private org.springframework.context.ApplicationContext springApplicationContext;
	private static ApplicationContext applicationContext;
	public static final String CONFIG_PATH = "config.path";
	public static final boolean IS_LINUX = System.getProperty("os.name").toLowerCase().startsWith("linux");
	public static final boolean IS_WINDOW = System.getProperty("os.name").toLowerCase().startsWith("Windows");

	public static ApplicationContext getInstance() {
		return applicationContext;
	}

	public static void initialiaze(org.springframework.context.ApplicationContext springApplicationContext) {
		applicationContext = new ApplicationContext(springApplicationContext);
	}

	public static void initialiaze(String configLocation, boolean reflash) {
		applicationContext = new ApplicationContext(configLocation, reflash);
	}

	private String configFileRootPath;
	private String[] configFilenames;
	private boolean reflash;

	private ApplicationContext(String configLocation, boolean reflash) throws BeansException {
		init();
		
		this.reflash = reflash;
		configFileRootPath = configLocation;
		refresh();
	}

	public ApplicationContext(org.springframework.context.ApplicationContext applicationContext) {
		springApplicationContext = applicationContext;
		this.reflash = false;
	}

	public void refresh() {
		if (!this.reflash) {
			return;
		}
		configFilenames = getAllSubFile(configFileRootPath);

		springApplicationContext = new FileSystemXmlApplicationContext(configFilenames, reflash);
	}

	public boolean containsBeanDefinition(String name) {
		return springApplicationContext.containsBeanDefinition(name);
	}

	public boolean containsBean(String name) {
		return springApplicationContext.containsBean(name);
	}

	public Object getBean(String name) {
		return springApplicationContext.getBean(name);
	}

	public Object getBean(String name, Object[] args) {
		return springApplicationContext.getBean(name, args);
	}

	private void init() {
		xmlFileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				String filename = f.getName().toLowerCase();
				if (filename.startsWith("."))
					return false;
				if (f.isDirectory())
					return true;

				if (filename.indexOf("beans") >= 0 && filename.endsWith(".xml")) {
					return true;
				} else
					return false;
			}
		};
	}

	private FileFilter xmlFileFilter;

	private String[] getAllSubFile(String path) {
		List<String> l = new ArrayList<String>();
		String[] paths = { path };
		if (path.lastIndexOf(";") > 0) {
			paths = path.split(";");
		}
		for (int i = 0; i < paths.length; ++i) {
			List<String> subFileNameList = getAllSubFileNameList(paths[i]);
			if (subFileNameList.size() > 0)
				l.addAll(subFileNameList);
		}
		String[] ret = new String[l.size()];
		l.toArray(ret);
		return ret;
	}

	private List<String> getAllSubFileNameList(String path) {
		List<String> ret = new ArrayList<String>();
		if (path == null)
			return ret;
		path = path.trim();
		if (path.length() == 0) {
			return ret;
		}
		File f = new File(path);
		if (f.isFile()) {
			ret.add(path);
			return ret;
		}
		ret = getAllSubFilenameList(f);
		return ret;
	}

	private List<String> getAllSubFilenameList(File f) {
		List<String> l = new LinkedList<String>();
		File[] subFiles = f.listFiles(xmlFileFilter);
		if (subFiles != null) {
			for (int i = 0; i < subFiles.length; ++i) {
				if (subFiles[i].isDirectory()) {
					l.addAll(getAllSubFilenameList(subFiles[i]));
				} else {
					String filename = subFiles[i].getAbsolutePath();
					if (IS_LINUX) {
						filename = File.separator + filename;
					}
					l.add(filename);
				}
			}
		}
		return l;
	}

}
