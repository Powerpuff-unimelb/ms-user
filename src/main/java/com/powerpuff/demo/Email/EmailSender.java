package com.powerpuff.demo.Email;

public interface EmailSender {

    String sendSimpleMail(EmailDetails details);

    String sendMailWithAttachment(EmailDetails details);

    String sendHtmlMail(EmailDetails details);
}
