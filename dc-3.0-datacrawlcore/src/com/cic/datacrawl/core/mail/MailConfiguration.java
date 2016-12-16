package com.cic.datacrawl.core.mail;

/*
 MAIL_HOST=smtp.cicdata.com
 MAIL_USER=cicdata@cicdata.com
 MAIL_PASSWD=123456
 MAIL_FROM=cicdata@cicdata.com
 */

public class MailConfiguration {
	private String host;
	private String account;
	private String password;
	private String from;
	private String textFormat;

	/**
	 * @return the textFormat
	 */
	public String getTextFormat() {
		return textFormat;
	}

	/**
	 * @param textFormat
	 *            the textFormat to set
	 */
	public void setTextFormat(String textFormat) {
		this.textFormat = textFormat == null ? null : textFormat.trim();
	}

	private boolean needAuthentication;

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the needAuthentication
	 */
	public boolean isNeedAuthentication() {
		return needAuthentication;
	}

	/**
	 * @param needAuthentication
	 *            the needAuthentication to set
	 */
	public void setNeedAuthentication(boolean needAuthentication) {
		this.needAuthentication = needAuthentication;
	}

}
