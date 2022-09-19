package com.powerpuff.demo.Email;

public interface EmailSender {

    String sendSimpleMail(EmailDetails details);

    String sendMailWithAttachment(EmailDetails details);
}
