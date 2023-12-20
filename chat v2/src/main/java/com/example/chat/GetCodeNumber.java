package com.example.chat;


import java.util.Properties;
import java.util.Random;
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
        // 发件人邮箱和密码
        String fromEmail = "305925092@qq.com";
        String password = "deedlnzfealrbgej";

        // 收件人邮箱
        String toEmail = email;

        // 生成随机验证码
        String verificationCode = generateVerificationCode();

        // 邮件主题和内容
        String subject = "验证码";
        String content = "您的验证码是：" + verificationCode;

        // 发送邮件
        sendEmail(fromEmail, password, toEmail, subject, content);
        return verificationCode;
    }

    private static void sendEmail(String fromEmail, String password, String toEmail, String subject, String content) {
        // 配置SMTP服务器和端口
        String smtpHost = "smtp.qq.com";
        int smtpPort = 587;

        // 创建Properties对象，设置邮件服务器相关信息
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtpHost);
        props.setProperty("mail.smtp.port", String.valueOf(smtpPort));
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");

        // 创建Session对象，并通过用户名和密码验证发件人账号
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // 创建MimeMessage对象
            MimeMessage message = new MimeMessage(session);

            // 设置发件人
            message.setFrom(new InternetAddress(fromEmail));

            // 设置收件人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            // 设置邮件主题
            message.setSubject(subject);

            // 设置邮件内容
            message.setText(content);

            // 发送邮件
            Transport.send(message);

            System.out.println("验证码已发送至邮箱：" + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private static String generateVerificationCode() {
        // 生成随机六位验证码
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}