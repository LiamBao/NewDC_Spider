package com.cic.datacrawl.core.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mozilla.javascript.xmlimpl.RhinoXmlUtil;
import org.xml.sax.SAXException;

import com.cic.datacrawl.core.entity.BaseEntity;
import com.cic.datacrawl.core.entity.DefaultEntity;

public class XMLUtil {
	public static String parseXMLValue(List<Object> list) {
		StringBuffer ret = new StringBuffer("<LIST>");
		String defaultNodeName = "ELEMENT";
		if (list != null) {
			// ret.append("<SIZE>");
			// ret.append(list.size());
			// ret.append("</SIZE>");
			for (int i = 0; i < list.size(); ++i) {
				Object o = list.get(i);
				String nodeName = null;
				if (!(o instanceof BaseEntity) && !(o instanceof Map)) {
					nodeName = defaultNodeName;
				}

				if (nodeName != null) {
					ret.append("<");
					ret.append(nodeName);
					ret.append(">");
				}
				ret.append(parseXMLValue(list.get(i)));
				if (nodeName != null) {
					ret.append("</");
					ret.append(nodeName);
					ret.append(">");
				}
			}
		}

		ret.append("</LIST>");
		return ret.toString();
	}

	public static String parseXMLValue(int num) {
		return "<![CDATA[" + num + "]]>";
	}

	public static String parseXMLValue(short num) {
		return "<![CDATA[" + num + "]]>";
	}

	public static String parseXMLValue(long num) {
		return "<![CDATA[" + num + "]]>";
	}

	public static String parseXMLValue(float num) {
		return "<![CDATA[" + num + "]]>";
	}

	public static String parseXMLValue(double num) {
		return "<![CDATA[" + num + "]]>";
	}

	public static String parseXMLValue(byte b) {
		return "<![CDATA[" + b + "]]>";
	}

	public static String parseXMLValue(Date date) {
		return "<![CDATA[" + DateUtil.formatTimestamp(date) + "]]>";
	}

	public static String parseXMLValue(char c) {
		return "<![CDATA[" + c + "]]>";
	}

	public static String parseXMLValue(Object o) {
		if (o == null)
			return "";
		if (o instanceof BaseEntity)
			return parseXMLValue((BaseEntity) o);

		if (o instanceof Date)
			return parseXMLValue((Date) o);
		if (o instanceof List)
			return parseXMLValue((List) o);
		if (o instanceof Map)
			return parseXMLValue((Map) o);

		if (o.getClass().isArray())
			return parseXMLValue((Object[]) o);

		return "<![CDATA[" + o.toString() + "]]>";
	}

	public static String parseXMLValue(BaseEntity entity) {
		return entity.toXMLString();
	}

	public static String parseXMLValue(Object[] array) {
		StringBuffer ret = new StringBuffer("<Array>");
		if (array != null) {
			for (int i = 0; i < array.length; ++i) {
				ret.append("<" + i + ">");
				ret.append(parseXMLValue(array[i]));
				ret.append("</" + i + ">");
			}
		}
		ret.append("</Array>");
		return ret.toString();
	}

	public static String parseXMLValue(Map<String, Object> map) {
		StringBuffer ret = new StringBuffer();
		String tagNameKey = "tag_name";
		String elementNameKey = "element_name";
		String tagName = null;
		String elementName = null;
		if (map != null && map.size() > 0) {
			String[] mapKeys = new String[map.size()];
			map.keySet().toArray(mapKeys);
			for (int i = 0; i < mapKeys.length && tagName == null; ++i) {
				if (tagNameKey.equalsIgnoreCase(mapKeys[i])) {
					tagName = (String) map.get(mapKeys[i]);
				}
				if (elementNameKey.equalsIgnoreCase(mapKeys[i])) {
					elementName = (String) map.get(mapKeys[i]);
				}
			}
			if (tagName == null || tagName.trim().length() == 0)
				tagName = "MAP";

			ret.append("<");
			ret.append(tagName);
			ret.append(">");

			// ret.append("<PROPERTY_NUMBER>");
			// ret.append(map.size());
			// ret.append("</PROPERTY_NUMBER>");
			for (int j = 0; j < map.size(); ++j) {
				if (!tagNameKey.equalsIgnoreCase(mapKeys[j]) && !elementNameKey.equalsIgnoreCase(mapKeys[j])) {
					boolean notHasElementName = elementName == null || elementName.trim().length() == 0;

					ret.append("<");
					ret.append(notHasElementName ? mapKeys[j] : elementName);
					ret.append(">");
					String content = parseXMLValue(map.get(mapKeys[j]));
					ret.append((content == null || content.trim().length() == 0) ? mapKeys[j] : content);
					ret.append("</");
					ret.append(notHasElementName ? mapKeys[j] : elementName);
					ret.append(">");
				}
			}
			ret.append("</");
			ret.append(tagName);
			ret.append(">");
		}
		return ret.toString();
	}

	public static String parseToText(String xmlContent, String charset, boolean isRecursion,
			boolean hideNotShowNode) throws SAXException, IOException, ParserConfigurationException {

		return RhinoXmlUtil.toPureText(RhinoXmlUtil.toXmlNodes(xmlContent, charset), isRecursion,
										hideNotShowNode);
	}

	/**
	 * 将节点名或属性名中的非法字符，统一替换为_
	 * 
	 * @param strName
	 * @return
	 */
	public static String normalizeName(String strName) {
		if (strName == null)
			return null;
		if (strName.length() == 0)
			return strName;

		char[] strChars = strName.toCharArray();
		char firstChar = strName.charAt(0);
		if (!(((firstChar >= 'a') && (firstChar <= 'z')) || ((firstChar >= 'A') && (firstChar <= 'Z')) || (firstChar == '_'))) {
			strChars = strName.toCharArray();
			strChars[0] = '_';
		}

		int len = strName.length();
		for (int i = 1; i < len; i++) {
			char c = strName.charAt(i);
			if (!(((c >= 'a') && (c <= 'z'))
					|| ((c >= 'A') && (c <= 'Z'))
					|| ((c >= '0') && (c <= '9'))
					|| (c == '_') || (c == '-'))) {
				if (strChars == null)
					strChars = strName.toCharArray();
				strChars[i] = '_';
			}
		}
		if (strChars == null)
			return strName;
		else
			return new String(strChars);
	}

	/**
	 * 属性值中特殊字符的转换
	 * 
	 * @param s
	 * @return
	 */
	public static String normalizeAttrValue(String s) {
		if (s == null)
			return null;
		StringBuilder str = new StringBuilder();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			normalizeChar(c, true, str);
		}
		return str.toString();
	}

	/**
	 * 文本中特殊字符的转换
	 * 
	 * @param s
	 * @return
	 */
	public static String normalizeText(String s) {
		if (s == null)
			return null;
		StringBuilder str = new StringBuilder();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			normalizeChar(c, false, str);
		}
		return str.toString();
	}

	/**
	 * 将XML中的特殊字符转换为易读的字符。比如将"&lt;"转换为 "<"，"&amp;"转换为"&"
	 * 
	 * @param s
	 * @return
	 */
	public static String replaceEasyReadChar(String str) {
		if (str == null)
			return null;
		String ret = str;
		String[][] sample = new String[][] { { "&quot;", "\"" }, { "&nbsp;", " " }, { "&#xD;", "\r" },
				{ "&lt;", "<" }, { "&gt;", ">" }, { "&amp;", "&" } };
		for (int i = 0; i < sample.length; ++i) {
			if (ret.indexOf(sample[i][0]) > 0) {
				StringBuffer sb = new StringBuffer();
				String[] s = StringUtil.fullSplit(ret, sample[i][0]);
				if (s != null) {
					for (int m = 0; m < s.length; ++m) {
						if (m > 0)
							sb.append(sample[i][1]);
						sb.append(s[m]);
					}
				}
				ret = sb.toString();
				// System.out.println("ret.indexOf("+sample[i][0]+") = "+ret.indexOf(sample[i][0]));
				// StringUtils.replace(ret,sample[i][0],
				// sample[i][1],sample[i][0].length());
			}
		}
		return ret;
	}

	public static void main(String[] args) throws DocumentException, FileNotFoundException {
		// String[] str = new String[] {
		// "1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;1&amp;2&quot;34567890&nbsp;"
		// };
		// for (int i = 0; i < str.length; ++i)
		// System.out.println(replaceEasyReadChar(str[i]));

		//		
		// String xmlFile = "C:/temp/baidu.xml";
		// Reader reader = null;
		// reader = new BufferedReader(new FileReader(xmlFile));
		// SAXReader saxReader = new SAXReader();
		//
		// Document document = saxReader.read(reader);
		// Element rootElement = document.getRootElement();
		// printElement(rootElement, 0);

		DefaultEntity child = new DefaultEntity("Child_Entity");
		child.set("String", "CS");
		child.set("bool", true);
		child.set("int", 6789);
		child.set("float", 123.45);
		child.set("date", new Date());
		child.set("date_DATEFORMAT", "yyyy/MM/dd HH.mm.ss.SSS");
		child.set("datetime", new Date());
		child.set("Byte", new Byte((byte) 83));

		DefaultEntity entity = new DefaultEntity("Test_Entity");
		entity.set("String", "this is string value");
		entity.set("bool", true);
		entity.set("int", 12345);
		entity.set("float", 323.555);
		entity.set("date", new Date());
		entity.set("date_DATEFORMAT", "yyyy/MM/dd HH.mm.ss.SSS");
		entity.set("datetime", new Date());
		entity.set("Byte", new Byte((byte) 23));
		entity.set("zzz", child);

		String xml = entity.toXMLString();
		BaseEntity e1 = buildEntity(xml);
		String xml2 = e1.toXMLString();
		System.out.println(xml);
		System.out.println(xml2);
		System.out.println(xml.equals(xml2));

	}

	private static void printElement(Element element, int depth) {
		StringBuilder startLine = new StringBuilder();
		for (int i = 0; i < depth; ++i) {
			startLine.append("\t");
		}
		String start = startLine.toString();
		System.out.print(start);
		System.out.println("Node name: " + element.getName());
		System.out.print(start);
		System.out.println("Node text: " + element.getText());
		for (int i = 0; i < element.attributeCount(); ++i) {
			Attribute attrByIndex = element.attribute(i);
			System.out.print(start + "\t");
			System.out.println("Node Attribute["
								+ i
								+ "]: {name = \""
								+ attrByIndex.getName()
								+ "\"; text = \""
								+ attrByIndex.getText()
								+ "\"; value = \""
								+ attrByIndex.getValue()
								+ "\"}");

			Attribute attrByName = element.attribute(attrByIndex.getName());
			System.out.print(start + "\t");
			System.out.println("Node Attribute["
								+ attrByIndex.getName()
								+ "]: {name = \""
								+ attrByName.getName()
								+ "\"; text = \""
								+ attrByName.getText()
								+ "\"; value = \""
								+ attrByName.getValue()
								+ "\"}");
		}
		System.out.println("-----------------------------------------"
							+ "------------------------------------------");

		List<Element> childrenElement = element.elements();
		for (int i = 0; i < childrenElement.size(); ++i) {
			printElement(childrenElement.get(i), depth + 1);
		}

	}

	private static void normalizeChar(char c, boolean isAttValue, StringBuilder str) {
		if ((c >= 0x7F && c <= 0x84)
			|| (c >= 0x86 && c <= 0x9F)
			|| (c >= 0xFFFE && c <= 0xFFFF)
			|| (c >= 0x1FFFE && c <= 0x1FFFF)
			|| (c >= 0x2FFFE && c <= 0x2FFFF)
			|| (c >= 0x3FFFE && c <= 0x3FFFF)
			|| (c >= 0x4FFFE && c <= 0x4FFFF)
			|| (c >= 0x5FFFE && c <= 0x5FFFF)
			|| (c >= 0x6FFFE && c <= 0x6FFFF)
			|| (c >= 0x7FFFE && c <= 0x7FFFF)
			|| (c >= 0x8FFFE && c <= 0x8FFFF)
			|| (c >= 0x9FFFE && c <= 0x9FFFF)
			|| (c >= 0xAFFFE && c <= 0xAFFFF)
			|| (c >= 0xBFFFE && c <= 0xBFFFF)
			|| (c >= 0xCFFFE && c <= 0xCFFFF)
			|| (c >= 0xDFFFE && c <= 0xDFFFF)
			|| (c >= 0xEFFFE && c <= 0xEFFFF)
			|| (c >= 0xFFFFE && c <= 0xFFFFF)
			|| (c >= 0x10FFFE && c <= 0x10FFFF)) {			
		} else {
			switch (c) {
			case '<': {
				str.append("&lt;");
				break;
			}
			case '>': {
				str.append("&gt;");
				break;
			}
			case '&': {
				str.append("&amp;");
				break;
			}
			case '"': {
				// A '"' that appears in character data
				// does not need to be escaped.
				if (isAttValue) {
					str.append("&quot;");
				} else {
					str.append("\"");
				}
				break;
			}
			case '\r': {
				// If CR is part of the document's content, it
				// must not be printed as a literal otherwise
				// it would be normalized to LF when the document
				// is reparsed.
				str.append("&#xD;");
				break;
			}
				/*
				 * case '\n': { if (fCanonical) { str.append("&#xA;"); break; }
				 * // else, default print char }
				 */
			default: {
				str.append(c);
			}
			}
		}
	}

	public static BaseEntity buildEntity(String xmlString) throws DocumentException {
		Reader reader = null;
		reader = new BufferedReader(new StringReader(xmlString));
		SAXReader saxReader = new SAXReader();

		Document document = saxReader.read(reader);
		Element rootElement = document.getRootElement();
		return buildEntity(rootElement);
	}

	@SuppressWarnings("unchecked")
	public static BaseEntity buildEntity(Element element) {
		if (element == null)
			return null;
		BaseEntity entity = new DefaultEntity(element.getName());

		List<Element> propertyNodes = element.elements();

		for (int i = 0; i < propertyNodes.size(); ++i) {
			Element propertyNode = propertyNodes.get(i);

			String propertyName = propertyNode.getName();

			Attribute type = propertyNode.attribute("type");
			if (type == null) {
				throw new RuntimeException("PropertyNode(index = "
											+ i
											+ ", name = \""
											+ propertyName
											+ "\") is invalid. Each propertyNode must have type attribute.");
			}
			String typeValue = type.getText();
			if ("Date".equalsIgnoreCase(typeValue)) {
				entity.set(propertyName, DateUtil.format(propertyNode.getText()));
/*
 * 对解析到的Date类型数据，不再按文件给出的format来格式化日期数据
 * 
 * 				String dateFormat = null;
				Attribute attributeDateFormat = propertyNode.attribute("format");
				if (attributeDateFormat != null) {
					dateFormat = attributeDateFormat.getValue();
				}

				if (dateFormat == null || dateFormat.length() == 0) {
					entity.set(propertyName, DateUtil.format(propertyNode.getText()));
				} else {
					entity.set(propertyName, DateUtil.format(propertyNode.getText(), dateFormat));
					entity.setDateFormat(propertyName, dateFormat);
				}
*/
			} else if ("String".equalsIgnoreCase(typeValue)) {
				entity.set(propertyName, propertyNode.getText());
			} else if ("BaseEntity".equalsIgnoreCase(typeValue)) {
				List<Element> elementList = propertyNode.elements();
				if (elementList.size() > 1) {
					throw new RuntimeException("PropertyNode(index = "
												+ i
												+ ", name = \""
												+ propertyName
												+ "\") has more than one element.");

				}
				if (elementList.size() == 1) {
					entity.set(propertyName, buildEntity(elementList.get(0)));
				}
			} else {
				Object value = null;
				try {
					Class clazz = Class.forName(typeValue);
					Constructor constructor = clazz.getConstructor(String.class);
					value = constructor.newInstance(propertyNode.getText());
				} catch (Exception e) {
					throw new RuntimeException("PropertyNode(index = "
												+ i
												+ ", name = \""
												+ propertyName
												+ "\") can not create instance by constructor: "
												+ typeValue
												+ "(String).", e);
				}

				entity.set(propertyName, value);
			}
		}

		return entity;
	}

}
