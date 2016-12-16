package com.cic.datacrawl.core.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.cic.datacrawl.core.config.Config;
import com.cic.datacrawl.core.util.StringUtil;

public class SaveEntityToXML extends EntitySaveManager {

	public SaveEntityToXML() {
		super();
	}

	public SaveEntityToXML(String outputPath) {
		this();
		this.outputPath = outputPath;

	}

	private static final SaveEntityToXML defaultSingleInstance = new SaveEntityToXML();

	public static final SaveEntityToXML getInstance() {
		return defaultSingleInstance;
	}

	private String outputPath;

	/**
	 * @param outputPath
	 *            the outputPath to set
	 */
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	/**
	 * @return the outputPath
	 */
	public String getOutputPath() {
		if (outputPath == null)
			outputPath = Config.getInstance().getOutputPath();
		return outputPath;
	}

	private Document document;
	private Element rootElement;

	private Element createRootNode() {
		if (document == null) {
			document = DocumentHelper.createDocument();
			return document.addElement("Entities");
		} else {
			synchronized (document) {
				document = DocumentHelper.createDocument();
				return document.addElement("Entities");
			}
		}
	}

	/**
	 * 将一个BaseEntity对象构建为一个xml element
	 * 
	 * @param entity
	 * @return
	 * @throws MalformedURLException
	 * @throws DocumentException
	 */
	public Element buildEntityElement(BaseEntity entity) throws MalformedURLException, DocumentException {
		Reader reader = null;
		reader = new BufferedReader(new StringReader(entity.toXMLString()));
		SAXReader saxReader = new SAXReader();

		Document document = saxReader.read(reader);
		return document.getRootElement();
	}

	private volatile int elementCount = 0;

	@Override
	public void save(BaseEntity entity) {
		if (entity == null)
			return;

		if (rootElement == null) {
			clean();
		}
		try {
			synchronized (rootElement) {
				rootElement.add(buildEntityElement(entity));
				++elementCount;
				++totalElementCount;
				if (elementCount >= getBufferSize()) {
					try {
						commit();
					} catch (Exception e) {
					}
				}

				LOG.debug("save Entity to xml: " + elementCount);
			}
		} catch (MalformedURLException e) {
		} catch (DocumentException e) {
		}
	}

	@Override
	public void save(BaseEntity[] entities) {
		if (entities != null) {
			for (int i = 0; i < entities.length; ++i) {
				save(entities[i]);
			}
		}
	}

	public static final String DATA_FORMAT = "yyyy-MM-dd HH_mm_ss_SSS";
	private String filenameStart = StringUtil.buildRandomString(10, StringUtil.UPPER_LOWER_AND_NUMBER_CHAR);
	private int index = 0;

	@Override
	public void clean() {
		if (rootElement == null) {
			rootElement = createRootNode();
		} else {
			synchronized (rootElement) {
				rootElement = createRootNode();
			}
		}
		elementCount = 0;
	}

	@Override
	public void commit() throws Exception {
		if (document == null) {
			return;
		} else {
			synchronized (document) {
				if (!document.hasContent()) {
					return;
				}
			}
		}
		XMLWriter writer = null;
		/** 格式化输出,类型IE浏览一样 */
		OutputFormat format = OutputFormat.createPrettyPrint();
		/** 指定XML编码 */
		format.setEncoding("UTF-8");
		String filename = null;
		File xmlFile = null;
		try {
			StringBuilder fileNameBuilder = new StringBuilder();
			fileNameBuilder.append(getOutputPath());
			if (!(getOutputPath().endsWith(File.pathSeparator) || getOutputPath().endsWith("/") || getOutputPath()
					.endsWith("\\"))) {

				fileNameBuilder.append(File.separator);
			}
			fileNameBuilder.append(filenameStart);
			if (index > 0) {
				fileNameBuilder.append("_");
				fileNameBuilder.append(index);
			}
			fileNameBuilder.append(".xml");

			filename = fileNameBuilder.toString();
			String backFilename = filename + ".tmp";
			index++;

			if (LOG.isDebugEnabled())
				LOG.debug("Start Commit to file: " + backFilename);

			xmlFile = new File(backFilename);
			File outputFolder = xmlFile.getParentFile();
			if (!outputFolder.exists()) {
				outputFolder.mkdirs();
			}

			if (!xmlFile.exists()) {
				xmlFile.createNewFile();
			}

			writer = new XMLWriter(new FileWriter(xmlFile), format);
			synchronized (document) {
				writer.write(document);
			}

			writer.flush();

			clean();
		} catch (IOException e) {
			throw e;
		} finally {
			if (writer != null)
				writer.close();

			if (xmlFile != null && filename != null)
				xmlFile.renameTo(new File(filename));

			// 解决内存泄漏问题
			xmlFile = null;

			LOG.info("Save to \"" + filename + "\" finished.");
			Runtime.getRuntime().gc();
		}
	}

}
