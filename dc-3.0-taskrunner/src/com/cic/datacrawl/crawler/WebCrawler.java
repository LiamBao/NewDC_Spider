package com.cic.datacrawl.crawler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cic.datacrawl.core.StatusCode;
import com.cic.datacrawl.core.StrPair;

public class WebCrawler {
	private static final Logger LOG = Logger.getLogger(WebCrawler.class);
	protected String charset = "UTF-8";
	
	protected String xmlContent;
	protected Document document;
	protected byte[] html;
	protected int httpStatus;
	protected int errorCode;
	protected String errorMessage;
	protected String webCharset;
	protected String userAgent;
	/**
	 * userGzip = 1 使用gzip的方式
	 * <Br>
	 * userGzip = 0 不使用gzip的方式
	 */
	protected int useGzip = 1;
	protected List<StrPair> invalidTagReplaceList;
	protected List<String> invalidAttrList;
	
	public String getHtmlContent() {
		if(html == null) {
			return "";
		} else {
			try {
				if(webCharset == null || webCharset.isEmpty()){
					LOG.warn("webCharset is null! set default value utf-8!");
					webCharset = "utf-8";
				}
				webCharset = webCharset.trim();
				return new String(html, webCharset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
		}
	}
	
	public String getXmlContent() {		
		return this.xmlContent;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getWebCharset() {
		return webCharset;
	}

	public void setWebCharset(String webCharset) {
		this.webCharset = webCharset;
	}
	
	public void setXmlCharset(String charset){
		this.charset = charset;
	}

	
	public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getUseGzip() {
        return useGzip;
    }

    public void setUseGzip(int useGzip) {
        this.useGzip = useGzip;
    }

    public void addInvalidTagReplacePair(String key, String value) {
		if(invalidTagReplaceList == null) {
			invalidTagReplaceList = new ArrayList<StrPair>();
		}
		
		StrPair pair = new StrPair();
		pair.setKey(key);
		pair.setValue(value);
		
		invalidTagReplaceList.add(pair);
	}
	
	public void addInvalidArrt(String attr) {
		if(invalidAttrList == null) {
			invalidAttrList = new ArrayList<String>();
		}
		
		invalidAttrList.add(attr);
	}
	
	protected void parsePageToDocument() {
	    
		String strHtml = new String(html);
		
		if(strHtml.indexOf("<html") == -1 && strHtml.indexOf("<HTML") == -1) {
			byte[] prefix = new String("<html xmlns=\"http://www.w3.org/1999/xhtml\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + webCharset + "\"/>").getBytes();
			byte[] surfix = new String("</html>").getBytes();
			byte[] all = new byte[prefix.length+html.length+surfix.length];
			
			System.arraycopy(prefix, 0, all, 0, prefix.length);
			System.arraycopy(html, 0, all, prefix.length, html.length);
			System.arraycopy(surfix, 0, all, prefix.length+html.length, surfix.length);
			
			html = all;
		}
		
		this.document = null;
		HTMLConfiguration config = new HTMLConfiguration();
		config.setFeature("http://cyberneko.org/html/features/balance-tags", true);
		config.setProperty("http://cyberneko.org/html/properties/names/elems", "upper");
		config.setProperty("http://cyberneko.org/html/properties/names/attrs", "upper");
		config.setProperty("http://cyberneko.org/html/properties/default-encoding", charset);
		config.setFeature("http://cyberneko.org/html/features/scanner/script/strip-comment-delims", true);
		config.setFeature("http://xml.org/sax/features/namespaces", false);
		DOMParser parser = new DOMParser(config);
		
		InputStream in = new ByteArrayInputStream(html);
		InputSource inPutSrc = new InputSource(in);
		try {
			parser.parse(inPutSrc);
			this.document = parser.getDocument();
		} catch (SAXException e) {
			this.errorCode = StatusCode.STATUS_TRANSFORMER_SAX_EXCEPTION;
			this.errorMessage = "SAXException when parse Page to Document";
			LOG.error("convertHtmlToXml failed: SaxException: " + e.getMessage());
		} catch (IOException e) {
			this.errorCode = StatusCode.STATUS_TRANSFORMER_IO_EXCEPTION;
			this.errorMessage = "IO Exception when parse Page to Document";
			LOG.error("convertHtmlToXml failed: IoException: " + e.getMessage());
		}
	}
	
	protected void convertPageToXml() {
		Transformer transformer = null;
		TransformerFactory tf=TransformerFactory.newInstance();
        try {
			transformer = tf.newTransformer();
	        DOMSource xmlSource = new DOMSource(this.document);
	        
	        Properties props = new Properties();
	        props.setProperty("encoding", charset);
	        props.setProperty("method", "xml");
	        props.setProperty("omit-xml-declaration", "yes");
	        transformer.setOutputProperties(props);
	        
	        StringWriter writer = new StringWriter();
	        StreamResult outputTarget = new StreamResult(writer);
	        try {
				transformer.transform(xmlSource, outputTarget);
				this.xmlContent = outputTarget.getWriter().toString();
				
				int offset = 0;		
				if((offset=this.xmlContent.indexOf("<HTML")) > 0) {
					this.xmlContent = this.xmlContent.substring(offset);
				}
				
				this.errorCode = StatusCode.STATUS_SUCC;
			} catch (TransformerException e) {
				this.errorCode = StatusCode.STATUS_TRANSFORMER_NORMAL_EXCEPTION;
				this.errorMessage = "TransformerException where convert page to XML";
				LOG.error("Transformer Exception: " + e.getMessage());
			}
		} catch (TransformerConfigurationException e) {
			this.errorCode = StatusCode.STATUS_TRANSFORMER_CONFIGURATION_EXCEPTION;
			this.errorMessage = "TransformerConfigurationException where convert page to XML";
			LOG.error("TransformerConfigurationException: " + e.getMessage());
		}
	}
	
	public void processInvalidCharacter() {
		Pattern pattern = Pattern.compile("&#[0-9]+;");
		Matcher matcher = pattern.matcher(this.xmlContent);
		this.xmlContent = matcher.replaceAll("");
	}
	
	public void processHtmlInvalidTag() {
		Pattern pattern = null;
		Matcher matcher = null;
		
		String strHtml = null;
		
		try {
			strHtml = new String(this.html, webCharset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if(strHtml != null) {
			if(invalidTagReplaceList != null && !invalidTagReplaceList.isEmpty()) {
				for(StrPair pair : invalidTagReplaceList) {
					pattern = Pattern.compile(pair.getKey());
					matcher = pattern.matcher(strHtml);
					strHtml = matcher.replaceAll(pair.getValue());
				}
			}
							
			//不能直接将 *：*="*" 的表达式直接删除，可能导致整个语句为空；也不能直接替换成某个表达式，否则可能重复
			if(invalidAttrList != null && !invalidAttrList.isEmpty()) {
				for(String attr : invalidAttrList) {
					pattern = Pattern.compile(attr);
					matcher = pattern.matcher(strHtml);
					Set<String> regexs = new HashSet<String>();
					while(matcher.find()) {
						String rs = matcher.group();
						regexs.add(rs);
					}
					for(String regex : regexs) {
						String str = regex.replace(':', '_');
						strHtml = strHtml.replaceAll(regex, str);
					}
				}
			}
			
			try {
				this.html = strHtml.getBytes(webCharset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void processXmlInvalidTag() {
		Pattern pattern = null;
		Matcher matcher = null;
		
		if(invalidTagReplaceList != null && !invalidTagReplaceList.isEmpty()) {
			for(StrPair pair : invalidTagReplaceList) {
				pattern = Pattern.compile(pair.getKey());
				matcher = pattern.matcher(this.xmlContent);
				this.xmlContent = matcher.replaceAll(pair.getValue());
			}
		}
						
		//不能直接将 *：*="*" 的表达式直接删除，可能导致整个语句为空；也不能直接替换成某个表达式，否则可能重复
		if(invalidAttrList != null && !invalidAttrList.isEmpty()) {
			for(String attr : invalidAttrList) {
				pattern = Pattern.compile(attr);
				matcher = pattern.matcher(this.xmlContent);
				Set<String> regexs = new HashSet<String>();
				while(matcher.find()) {
					String rs = matcher.group();
					regexs.add(rs);
				}
				for(String regex : regexs) {
					String str = regex.replace(':', '_');
					this.xmlContent = this.xmlContent.replaceAll(regex, str);
				}
			}
		}
	}
	
	/**
     * 去除字符串特殊字符
     * */
    public String stripNonCharCodepoints(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder retval = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch % 0x10000 != 0xffff && // 0xffff - 0x10ffff range step
                ch % 0x10000 != 0xfffe && // 0xfffe - 0x10fffe range
                (ch <= 0xfdd0 || ch >= 0xfdef) && // 0xfdd0 - 0xfdef
                (ch <= 55295 || ch >= 57344) && // mysql 不支持字符
                (ch > 0x1F || ch == 0x9 || ch == 0xa || ch == 0xd))
            {
                retval.append(ch);
            }
        }
        return retval.toString();
    }
}
