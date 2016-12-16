package org.mozilla.javascript.xmlimpl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mozilla.javascript.xml.XMLObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 因org.mozilla.javascript.xmlimpl.*类不public，此类用于相关代理转换
 */
public class RhinoXmlUtil {
	public static boolean instanceofXML(Object obj) {
		if (obj == null)
			return false;
		return (obj instanceof XML);
	}

	public static boolean instanceofXMLList(Object obj) {
		if (obj == null)
			return false;
		return (obj instanceof XMLList);
	}

	public static String getNodeType(Object obj) {
		if (obj == null)
			return "empty";
		if (instanceofXML(obj)) {
			XML xml = (XML) obj;
			if (xml.isAttribute()) {
				return "attribute";
			} else if (xml.isComment()) {
				return "comment";
			} else if (xml.isElement()) {
				return "node";
			} else if (xml.isText()) {
				return "text";
			}
		} else if (instanceofXMLList(obj)) {
			return "list";
		}
		return "empty";
	}

	/**
	 * 获取XML/XMLList的内容(xml格式)
	 * 
	 * @param obj
	 * @return
	 */
	public static String toXmlString(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof XMLObjectImpl)
			return ((XMLObjectImpl) obj).toXMLString();
		throw new RuntimeException("Error at type convert from "
									+ obj.getClass().getName()
									+ " to XML/XMLList.");
	}

	/**
	 * 获取XML/XMLList的节点(w3c dom格式)
	 * 
	 * @param obj
	 * @return
	 */
	public static Node[] toXmlNodes(XMLObject obj) {
		if (obj == null)
			return null;

		if (obj instanceof XMLList) {
			XMLList list = (XMLList) obj;
			Node[] result = new Node[list.length()];
			for (int i = 0; i < result.length; i++) {
				result[i] = ((XML) list.get(i, null)).toDomNode();
			}
			return result;
		} else if (obj instanceof XML)
			return new Node[] { ((XML) obj).toDomNode() };
		throw new RuntimeException("Error at type convert from "
									+ obj.getClass().getName()
									+ " to XML/XMLList.");
	}

	/**
	 * 获取XML/XMLList的节点(w3c dom格式)
	 * 
	 * @param obj
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Node[] toXmlNodes(String xmlString, String charset) throws SAXException, IOException,
			ParserConfigurationException {
		BufferedInputStream input = null;

		input = new BufferedInputStream(new ByteArrayInputStream(xmlString.getBytes(charset)));

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

		NodeList nodeList = document.getChildNodes();
		int size = 0;
		Node[] nodes = null;
		if (nodeList != null) {
			size = nodeList.getLength();
			nodes = new Node[size];
			for (int i = 0; i < size; ++i) {
				nodes[i] = nodeList.item(i);
			}
		} else {
			nodes = new Node[0];
		}
		return nodes;
	}

	/**
	 * 获取XML/XMLList的纯文本内容
	 * 
	 * @param obj
	 * @return
	 */
	public static String toPureText(Node[] nodes, boolean isRecursion, boolean hideNotShowNode) {
		if (nodes == null)
			return null;
		if (nodes.length == 0)
			return "";

		StringBuilder ts = new StringBuilder();
		for (int i = 0; i < nodes.length; i++) {
			node2text(nodes[i], ts, isRecursion, hideNotShowNode);
		}
		return ts.toString().trim();
	}

	/**
	 * 获取XML/XMLList的纯文本内容
	 * 
	 * @param obj
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static String toPureText(String xmlString, String charset) throws SAXException, IOException,
			ParserConfigurationException {

		return toPureText(toXmlNodes(xmlString, charset), true, false);

	}

	/**
	 * 获取XML/XMLList的纯文本内容
	 * 
	 * @param obj
	 * @return
	 */
	public static String toPureText(XMLObject obj) {
		return toPureText(toXmlNodes(obj), true, false);
	}

	/**
	 * 获取XML/XMLList的纯文本内容
	 * 
	 * @param obj
	 * @return
	 */
	public static String toPureText(XMLObject obj, boolean isRecursion, boolean hideNotShowNode) {
		return toPureText(toXmlNodes(obj), isRecursion, hideNotShowNode);
	}

	/**
	 * 获取XML/XMLList的纯文本内容
	 * 
	 * @param obj
	 * @return
	 */
	public static String toPureText(XMLObject obj, boolean isRecursion) {
		return toPureText(obj, isRecursion, false);
	}

	private static void node2text(Node node, StringBuilder ts, boolean isRecursion, boolean hideNotShowNode) {
		if (isTabNode(node)) {
			ts.append("\t");
		}

		if (isNewLineNode(node)) {
			ts.append("\n");
		}

		if (isTextNode(node)) {
			ts.append(htmlTrim(node.getNodeValue()));
		} else {
			if (isValidNode(node, hideNotShowNode)) {
				if (node.hasChildNodes()) {
					NodeList childNodeList = node.getChildNodes();
					if (childNodeList != null)
						for (int i = 0; i < childNodeList.getLength(); ++i) {
							boolean isTextNode = isTextNode(childNodeList.item(i));
							if (isTextNode || (!isTextNode && isRecursion))
								node2text(childNodeList.item(i), ts, isRecursion, hideNotShowNode);
						}
				}
			}
		}
	}

	private static boolean isTabNode(Node node) {
		if (node == null)
			return false;
		String[] invalidNodeNames = { "td" };
		for (int i = 0; i < invalidNodeNames.length; ++i) {
			if (node.getNodeName().equalsIgnoreCase(invalidNodeNames[i])) {
				return true;
			}
		}
		return false;
	}

	private static boolean isNewLineNode(Node node) {
		if (node == null)
			return false;
		String[] invalidNodeNames = { "br", "p", "tr", "table", "li", "hr" };
		for (int i = 0; i < invalidNodeNames.length; ++i) {
			if (node.getNodeName().equalsIgnoreCase(invalidNodeNames[i])) {
				return true;
			}
		}
		return false;
	}

	private static boolean isTextNode(Node node) {
		return node.getNodeType() == Node.CDATA_SECTION_NODE || node.getNodeType() == Node.TEXT_NODE;
	}

	private static boolean isValidNode(Node node, boolean hideNotShowNode) {
		if (node == null)
			return false;
		String[] invalidNodeNames = { "script", "head", "STYLE" };
		for (int i = 0; i < invalidNodeNames.length; ++i) {
			if (node.getNodeName().equalsIgnoreCase(invalidNodeNames[i]) && !(node instanceof Attr)) {
				return false;
			}
		}
		if (hideNotShowNode) {
			NamedNodeMap attributes = node.getAttributes();
			if (attributes != null) {
				Node styleNode = attributes.getNamedItem("STYLE");
				if (styleNode != null) {
					String nodeValue = styleNode.getNodeValue().toLowerCase();
					if (nodeValue.indexOf("display: none") >= 0
						|| nodeValue.indexOf("display:none") >= 0
						|| nodeValue.indexOf("font-size: 0") >= 0
						|| nodeValue.indexOf("font-size:0") >= 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static String htmlTrim(String str) {
		char[] val = str.toCharArray();
		int len = val.length;
		int st = 0;
		int off = 0;

		while (st < len) {
			char tc = val[off + st];
			if ((tc <= ' ') || (tc == 160/* HTML a non-breaking space */))
				st++;
			else
				break;
		}
		while (st < len) {
			char tc = val[off + len - 1];
			if ((tc <= ' ') || (tc == 160/* HTML a non-breaking space */))
				len--;
			else
				break;
		}
		return ((st > 0) || (len < len)) ? str.substring(st, len) : str;
	}

	/*
	 * org.mozilla.javascript.xmlimpl.XMLName.addMatches(...)中添加以下代码： } else {
	 * //add by long.li VVVVVVVVVVVVVVVVVVVV if ("$".equals(localName())) {
	 * rv.addToList(target); rv.setTargets(target, this.toQname()); return; }
	 * //add by long.li ^^^^^^^^^^^^^^^^^^^^
	 * 
	 * XML[] children = target.getChildren();
	 */

}
