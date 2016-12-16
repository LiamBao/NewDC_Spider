package com.cic.datacrawl.core.browser.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.cic.datacrawl.core.util.XMLUtil;
import com.cic.datacrawl.ui.beans.XmlTreeNode;

public class DomNode {
	public String nodeName;
	public HashMap<String, String> attributes;
	public ArrayList<DomNode> childNodes = new ArrayList<DomNode>();
	private String nodeValue;

	/**
	 * @return the nodeValue
	 */
	public String getNodeValue() {
		return nodeValue;
	}

	/**
	 * @param nodeValue
	 *            the nodeValue to set
	 */
	public void setNodeValue(String nodeValue) {
		this.nodeValue = XMLUtil.replaceEasyReadChar(nodeValue);
	}

	private boolean isInvalidCommentChar(char c) {
		char[] invalidChars = new char[] { '-' };
		for (int i = 0; i < invalidChars.length; ++i) {
			if (c == invalidChars[i])
				return true;
		}
		return false;
	}

	private String formatComment(String commentStr) {
		if (commentStr == null || commentStr.length() == 0)
			return commentStr;
		StringBuilder sb = new StringBuilder(commentStr);
		int oldEnd = commentStr.length();
		int start = 0;
		while (start < oldEnd && isInvalidCommentChar(sb.charAt(start))) {
			++start;
		}
		int end = sb.length();
		while ((end - 1 >= 0) && isInvalidCommentChar(sb.charAt(end - 1))) {
			--end;
		}
		if (start == 0 && end == oldEnd) {
			return commentStr;
		}
		if (start > 0) {
			if (start >= end) {
				return "";
			} else {
				if (end < oldEnd) {
					return sb.substring(start, end);
				} else {
					return sb.substring(start);
				}
			}

		} else if (end < oldEnd) {

			return sb.substring(0, end);
		}
		return sb.toString();
	}

	private Pattern pattern = Pattern.compile("\\p{Cntrl}", Pattern.DOTALL);

	public void toXml(StringBuilder xml, int depth, XmlTreeNode treeNode) {
		if ("#text".equals(nodeName)) {
			String ts = nodeValue.trim();
			if (ts.length() == 0)
				return;
			ts = XMLUtil.normalizeText(ts);
			if (ts != null) {
				ts = pattern.matcher(ts).replaceAll("");
			}
			xml.append(ts);
			return;
		} else if ("#comment".equals(nodeName)) {
			xml.append("<!--");
			String ts = nodeValue;
			if (ts != null) {
				ts = pattern.matcher(ts).replaceAll("");
			}
			xml.append(formatComment(ts));
			xml.append("-->");
			return;
		}

		String tsNodeName = XMLUtil.normalizeName(nodeName); // 部分网页的节点名不符合xml规范
		for (int i = 0; i < depth; i++)
			xml.append("  ");
		if (treeNode != null) {
			treeNode.setNodeName(tsNodeName.toUpperCase());
			treeNode.setTxtOffset(xml.length());
		}
		xml.append('<');
		xml.append(tsNodeName);

		if (attributes != null) {
			HashSet<String> attrNames = new HashSet<String>();
			Iterator<String> keys = attributes.keySet().iterator();
			ArrayList<XmlTreeNode> attributeNodeList = new ArrayList<XmlTreeNode>();
			while (keys.hasNext()) {
				XmlTreeNode attrNode = null;
				String attrName = (String) keys.next();
				String attrValue = attributes.get(attrName);
				if (attrValue != null) {
					attrValue = pattern.matcher(attrValue).replaceAll("");
					attrNode = new XmlTreeNode();
					attrNode.setNodeName(attrName.toUpperCase());
					attrNode.setNodeValue(attrValue);
					attributeNodeList.add(attrNode);
				}
				attrName = XMLUtil.normalizeName(attrName); // 部分网页的属性名不符合xml规范
				attrName = attrName.toUpperCase(); // 属性名统一转换为大写，以方便E4X访问
				attrName = genAttrName(attrName, attrNames); // 避免属性重名,自动添加后缀

				attrValue = XMLUtil.normalizeAttrValue(attrValue);				
				
				xml.append(' ');
				xml.append(attrName);
				xml.append("=\"");
				// xml.append(XMLUtil.replaceEasyReadChar(attrValue));
				xml.append(attrValue);
				xml.append("\"");

				if (treeNode != null) {
					if ("TITLE".equals(attrName))
						treeNode.setNodeTitle(attrValue);
					if ("ID".equals(attrName))
						treeNode.setNodeId(attrValue);
					if ("NAME".equals(attrName))
						treeNode.setNodeHtmlName(attrValue);
					if ("CLASS".equals(attrName))
						treeNode.setNodeClass(attrValue);
				}
			}
			if(treeNode != null && attributeNodeList.size() > 0){
				XmlTreeNode[] attributeNodes =new XmlTreeNode[attributeNodeList.size()]; 
				attributeNodeList.toArray(attributeNodes);
				treeNode.setAttributeNodes(attributeNodes);
			}
		}

		if (childNodes != null) {
			xml.append(">\n");
			for (int i = 0; i < childNodes.size(); i++) {
				XmlTreeNode childTreeNode = null;
				if (treeNode != null)
					childTreeNode = new XmlTreeNode();
				childNodes.get(i).toXml(xml, depth + 1, childTreeNode);
				if (treeNode != null) {
					if (childTreeNode.getNodeName() != null)
						treeNode.add(childTreeNode);
				}
				xml.append("\n");
			}
			xml.append("</" + tsNodeName + ">");
		} else if ((nodeValue != null) && (nodeValue.trim().length() > 0)) {
			String ts = nodeValue.trim();
			if (ts != null) {
				ts = pattern.matcher(ts).replaceAll("");
			}
			ts = XMLUtil.normalizeText(ts);
			xml.append(">\n");
			xml.append(ts);
			xml.append("</" + tsNodeName + ">");
		} else
			xml.append("/>");

		if (treeNode != null)
			treeNode.setTxtLength(xml.length() - treeNode.getTxtOffset());
	}

	// 避免属性重名,自动添加后缀
	private static String genAttrName(String attrName, HashSet<String> attrNames) {
		if (!attrNames.contains(attrName)) {
			attrNames.add(attrName);
			return attrName;
		}
		for (int i = 2; i < Integer.MAX_VALUE; i++) {
			if (!attrNames.contains(attrName + i)) {
				attrName = attrName + i;
				attrNames.add(attrName);
				return attrName;
			}
		}
		throw new RuntimeException("Error attrName");
	}

	public String toString() {
		StringBuilder xml = new StringBuilder();
		this.toXml(xml, 0, null);
		return xml.toString();
	}
}
