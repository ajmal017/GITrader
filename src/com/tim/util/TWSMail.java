package com.tim.util;

import java.lang.reflect.Array;
import java.security.Security;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.security.auth.Subject;

import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.service.TIMApiGITrader;

public class TWSMail {
	
	private String FROM="";
	private String[] TO;
	private String SMTP_HOST_NAME="smtp.gmail.com";
	private String SMTP_PORT="587";
	private String ACCOUNT_NAME="dnevado@gmail.com";
	private String ACCOUNT_PWD="gmaildnm";
	private String BODY="";
	private String SUBJECT="";
	private String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	
	public  TWSMail(String[] _To, String _Subject, String _Body)
	{
		this.TO = _To;
		this.SUBJECT = _Subject;
		this.BODY = _Body;
		
	}
	
	public void TWSMailSend()
	{
		
	}
	public void SendMail()
	{
	try
	{
		this.SendMailProvider();
	}
	catch (Exception e)
	{
		LogTWM.getLog(TIMApiGITrader.class);    	
		LogTWM.log(Priority.ERROR, "Error Enviando Mail" + e.getMessage());
	}
}
	private void SendMailProvider() throws MessagingException
	{
		
		boolean debug = true;
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.auth", "true");  
		props.put("mail.smtp.EnableSSL.enable","true");
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   
		props.setProperty("mail.smtp.socketFactory.fallback", "false");   
		props.setProperty("mail.smtp.port", "465");   
		props.setProperty("mail.smtp.socketFactory.port", "465"); 
		
		
		
		Session session = Session.getDefaultInstance(props,
		new javax.mail.Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(ACCOUNT_NAME,ACCOUNT_PWD);
		}
		});

		session.setDebug(debug);

		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(ACCOUNT_NAME);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[TO.length];
		for (int i = 0; i < TO.length; i++) {
			addressTo[i] = new InternetAddress(TO[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(SUBJECT);
		msg.setContent(BODY, "text/plain");
		Transport.send(msg);
		}

		public static void main(String[] args) throws InterruptedException, MessagingException {
			// TODO Auto-generated method stub
			String[] Recipients = {"david_nevado@yahoo.es"};
			TWSMail Mail = new TWSMail(Recipients, "HOLA", "HOLA");
			Mail.SendMailProvider();
		}	
	



}
