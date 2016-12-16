package com.cic.datacrawl.core;

public class StatusCode {
	public static final int STATUS_SUCC = 0;
	
	//browser 错误码
	public final static int BROWSER_RESP_STATUS_TIMEOUT = 901;				//Browser 下载超时
	public static final int BROWSER_RESP_STATUS_NOT_ACCESS = 902;			//Browser 下载时被禁止访问
	
	public static final int STATUS_INVALID_CLIENT_TYPE = 10000;				//错误的下载客户端类型（browser & httpClient），其中downloader有部分接口只能适用于browser
	//HttpClient 返回码
	public static final int STATUS_HTTP_INNER_SAVE = 10001;					//HttpClient下载时的status错误码小于200
	public static final int STATUS_HTTP_RES_BROKEN = 10002;					//HttpClient下载时的status错误码大于200，小于300
	public static final int STATUS_HTTP_REQ_REDIRECT = 10003;				//HttpClient下载时的status错误码大于等于300, 小于400
	public static final int STATUS_HTTP_REQ_FORBIDDEN = 10004;				//HttpClient下载时的status错误码大于等于400， 小于500
	public static final int STATUS_HTTP_ROUTE_ERR = 10005;					//HttpClient下载时的status错误码大于等于500， 小于600
	public static final int STATUS_HTTP_UNDEFIND_ERR = 10006;				//HttpClient下载时的status错误码大于600
	public static final int STATUS_HTTP_INVALID_HTML = 10007; 				//表示不是html格式文档
	public static final int STATUS_HTTP_NONE_POST_PARAMETER = 10008; 		//使用POST方法，但没有提供POST数据
	public static final int STATUS_HTTP_PARSE_COOKIE_FAILED = 10009;		//获取登录cookie失败
	public static final int STATUS_HTTP_IO_EXCEPTION = 10030;				//httpClient读取数据失败（IO异常）
	public static final int STATUS_STRING_TO_BYTEARRAY_EXCEPTION = 10031;	//String转byte[]时UnsupportedEncodingException异常
	public static final int STATUS_SNAPSHOT_NOT_EXIST = 10080;				//网页快照页面不存在
	
	//HtmlToXml 异常错误码
	public static final int STATUS_TRANSFORMER_NORMAL_EXCEPTION = 10101;	//html转换XML时TransformerException异常
	public static final int STATUS_TRANSFORMER_SAX_EXCEPTION = 10102;		//html转换document时SAXException异常
	public static final int STATUS_TRANSFORMER_IO_EXCEPTION = 10103;		//html转换XML时IO异常
	public static final int STATUS_TRANSFORMER_CONFIGURATION_EXCEPTION = 10104; //html转换XML时TransformerConfigurationException异常
	
	//登录失败错误码
	public static final int STATUS_LOGIN_ALLOC_ACCOUNT_FAILED = 10201;		//分配登录账号失败
	public static final int STATUS_LOGIN_CHANGE_ACCOUNT_FAILED = 10202;		//更换登录账号失败
	public static final int STATUS_LOGIN_SAVE_COOKIE_FAILED = 10203;		//保存共享的cookie失败
	public static final int STATUS_LOGIN_RECYCLE_ACCOUNT_FAILED = 10204;	//循环利用账号失效
	
	//解析失败错误码
	public static final int STATUS_PARSE_WITHOUT_RESULT_NODE = 10301;		//解析xml未发现结果节点（没有解析到结果记录，或者解析的e4x表达式已经失效）
	public static final int STATUS_PARSE_INVALID_RESULT_NODE = 10302;		//无效的解决节点
	public static final int STATUS_PARSE_INVALID_XML_NODE = 10303;			//无效的XML
	public static final int STATUS_PARSE_CURRENTPAGEINDEX_ZERO = 10304;		//解析到当前网页的页码为零（空）
	
	//脚本错误码
	public static final int STATUS_SCRIPT_INVALID_OBJECT = 10400;			//无效对象
	public static final int STATUS_SCRIPT_INVALID_URL = 10401;				//无效URL
	public static final int STATUS_SCRIPT_INVALID_PAGECHECKFUN = 10402;		//无效的页码检查函数
	public static final int STATUS_SCRIPT_INVALID_SITEID = 10403;			//无效的站点ID
	public static final int STATUS_SCRIPT_INVALID_ACCOUNTNODEEXP = 10404;	//无效账号输入节点E4X表达式
	public static final int STATUS_SCRIPT_INVALID_PASSWDNODEEXP = 10405;	//无效密码输入节点E4X表达式
	public static final int STATUS_SCRIPT_INVALID_BUTTONNODEEXP = 10406;	//无效提交按钮节点E4X表达式
	public static final int STATUS_SCRIPT_INVALID_JAVASCRIPT = 10407;		//无效的javascript
	public static final int STATUS_SCRIPT_INVALID_PAGEELEMENT = 10408;		//无效的页面元素
	public static final int STATUS_SCRIPT_INVALID_AJAXWAIT = 10409;			//无效的ajaxWait表达式
	public static final int STATUS_SCRIPT_INVALID_XML_FORMAT = 10410;		//无效的XML格式
	public static final int STATUS_SCRIPT_INVALID_STRING_FORMAT = 10411;	//无效的String格式
	public static final int STATUS_SCRIPT_INVALID_DATE_FORMAT = 10412;		//无效的Date格式
	public static final int STATUS_SCRIPT_INVALID_PARSEFIRSTPOSTFUN = 10413;	//无效的解析Topic页面函数
	public static final int STATUS_SCRIPT_INVALID_VALUE = 10415;			//无效的值
	public static final int STATUS_SCRIPT_PARSE_XML_FORMAT = 10416;			//解析XML格式出错
	
	public static final int STATUS_SCRIPT_LOGIN_FAILED = 10450;				//脚本中执行登录失败
	
	//FQ异常错误码
	public static final int STATIS_FQ_INIT_FAILED = 10501;
	
	//TaskRunner异常错误码
	public static final int STATUS_TASKRUNNER_EXEJS_FAILED = 10901;			//执行JS失败
	public static final int STATUS_TASKRUNNER_LACK_ARGUMENT = 10902;		//TaskRunner执行缺少参数
	public static final int STATUS_TASKRUNNER_COMMIT_FAILED = 10903;		//提交保存采集的记录失败
	public static final int STATUS_TASKRUNNER_UNKNOWN_ERROR = 11000;		//未知错误信息
	
	public static String getStatusMessage(int statusCode) {
		switch(statusCode) {
		case STATUS_SUCC:
			return "STATUS_SUCC";
		case STATUS_INVALID_CLIENT_TYPE:
			return "STATUS_INVALID_CLIENT_TYPE";
		case STATUS_HTTP_INNER_SAVE:
			return "STATUS_HTTP_INNER_SAVE";
		case STATUS_HTTP_RES_BROKEN:
			return "STATUS_HTTP_RES_BROKEN";
		case STATUS_HTTP_REQ_REDIRECT:
			return "STATUS_HTTP_REQ_REDIRECT";
		case STATUS_HTTP_REQ_FORBIDDEN:
			return "STATUS_HTTP_REQ_FORBIDDEN";
		case STATUS_HTTP_ROUTE_ERR:
			return "STATUS_HTTP_ROUTE_ERR";
		case STATUS_HTTP_UNDEFIND_ERR:
			return "STATUS_HTTP_UNDEFIND_ERR";
		case STATUS_HTTP_IO_EXCEPTION:
			return "STATUS_HTTP_IO_EXCEPTION";
		case STATUS_TRANSFORMER_NORMAL_EXCEPTION:
			return "STATUS_TRANSFORMER_NORMAL_EXCEPTION";
		case STATUS_TRANSFORMER_SAX_EXCEPTION:
			return "STATUS_TRANSFORMER_SAX_EXCEPTION";
		case STATUS_TRANSFORMER_IO_EXCEPTION:
			return "STATUS_TRANSFORMER_IO_EXCEPTION";
		case STATUS_TRANSFORMER_CONFIGURATION_EXCEPTION:
			return "STATUS_TRANSFORMER_CONFIGURATION_EXCEPTION";
		
		default:
			return "UNDEFIND_STATUS_CODE";
		}
	}
}
