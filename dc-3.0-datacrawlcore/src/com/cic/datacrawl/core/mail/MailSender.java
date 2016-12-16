package com.cic.datacrawl.core.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.log4j.Logger;

public class MailSender {
	private static final Logger LOGGER = Logger.getLogger(MailSender.class);
	private static final String mailer = "JavaMail API";

	/**
	 * @param to
	 *            :接收者列表
	 * @param subject
	 *            : 邮件主题
	 * @param body
	 *            : 邮件内容
	 * @param mailConfig
	 *            : 邮件发送的配置文件
	 */
	public static boolean sendMessage(String to, String subject, String body,
			MailConfiguration mailConfig) {

		if (mailConfig == null) {
			LOGGER.error("Mail config is null.");
			return false;
		}
		if (to == null) {
			LOGGER.error("Mail address is null.");
			return false;
		}
		to = to.trim();
		if (to.length() == 0) {
			LOGGER.error("Mail address is empty.");
			return false;
		}

		Transport tr = null;

		Properties props = System.getProperties();
		if (mailConfig.isNeedAuthentication()) {
			props.put("mail.smtp.auth", "true");
		}
		// Get a Session object
		Session mailSession = Session.getDefaultInstance(props, null);
		boolean ret = false;

		// construct the message
		Message msg = new MimeMessage(mailSession);
		try {
			msg.setFrom(new InternetAddress(mailConfig.getFrom()));

			if (to.indexOf(";") >= 0) {
				String[] mailTos = to.split(";");
				InternetAddress[] ia = new InternetAddress[mailTos.length];
				for (int i = 0; i < ia.length; i++) {
					ia[i] = new InternetAddress(mailTos[i].trim());
				}

				msg.setRecipients(RecipientType.TO, ia);
			} else {
				msg.setRecipient(RecipientType.TO, new InternetAddress(to));
			}

			msg.setSubject(subject);
			if (mailConfig.getTextFormat() == null
					|| mailConfig.getTextFormat().length() == 0) {
				msg.setText(body);
			} else {
				MimeMultipart part = new MimeMultipart();
				MimeBodyPart bodyPart = new MimeBodyPart();
				bodyPart.setContent(body, mailConfig.getTextFormat());
				part.addBodyPart(bodyPart);
				msg.setContent(part);
			}
			msg.setHeader("X-Mailer", mailer);
			msg.setSentDate(new Date());
			msg.saveChanges();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			msg = null;
			mailSession = null;
			return ret;
		}
		try {
			tr = mailSession.getTransport("smtp");
			String server = mailConfig.getHost();
			String account = mailConfig.getAccount();
			String passwd = mailConfig.getPassword();
			tr.connect(server, account, passwd);
			tr.sendMessage(msg, msg.getAllRecipients());

			ret = true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (tr != null)
				try {
					tr.close();
				} catch (MessagingException e) {
					LOGGER.error(e.getMessage(), e);
				}
		}
		tr = null;
		msg = null;
		mailSession = null;
		return ret;
	}
}
