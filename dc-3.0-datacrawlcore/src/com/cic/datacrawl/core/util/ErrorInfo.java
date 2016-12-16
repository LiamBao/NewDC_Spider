/*****************************************************************<br>
 * <B>FILE :</B> ErrorInfo.java <br>
 * <B>CREATE DATE :</B> 2012-3-21 <br>
 * <B>DESCRIPTION :</B> <br>
 *
 * <B>CHANGE HISTORY LOG</B><br>
 *---------------------------------------------------------------<br>
 * NO.  |  DATE |   NAME   |   REASON   |  DESCRIPTION           <br>
 *---------------------------------------------------------------<br>
 *          
 *****************************************************************<br>
 */
package com.cic.datacrawl.core.util;

/**
 * <B>Function :</B> <br>
 * <B>General Usage :</B> <br>
 * <B>Special Usage :</B> <br>
 * 
 * @author : jean.jiang<br>
 * @since : 2012-3-21<br>
 * @version : v1.0
 */
public class ErrorInfo {
	private String errorCode;
	private String errorMsg;
	private String errorIds;
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @return the errorIds
	 */
	public String getErrorIds() {
		return errorIds;
	}
	/**
	 * @param errorIds the errorIds to set
	 */
	public void setErrorIds(String errorIds) {
		this.errorIds = errorIds;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
