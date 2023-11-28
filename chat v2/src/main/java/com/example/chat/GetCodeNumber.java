package com.example.chat;


import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GetCodeNumber {
  public static String GetNumber(String email) {
    String code = send("contract@yangzhijiemysql.top",email,"wj20031012","smtp.yangzhijiemysql.top");
    return code;
  }

  private static String send(String sendEmail, String receiveEmail, String sendPassword, String host) {
    Properties props = new Properties();
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.auth", "true");
    String code= String.valueOf((int)((Math.random() * 9 + 1) * 100000));
    Session session = Session.getDefaultInstance(props, new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(sendEmail, sendPassword);
      }
    });

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(sendEmail));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveEmail));
      message.setSubject("Hello from JavaMail");
      message.setText("This is a test email sent from JavaMail."+code);

      Transport.send(message);
      System.out.println("Email sent successfully!");
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    return code;
  }

}
