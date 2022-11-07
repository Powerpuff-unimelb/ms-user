package com.powerpuff.demo.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Objects;


@Service
public class EmailService implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String sendSimpleMail(EmailDetails details) {

        try {

            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        } catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    public String sendMailWithAttachment(EmailDetails details) {
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(
                    details.getSubject());

            FileSystemResource file
                    = new FileSystemResource(
                    new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(
                    Objects.requireNonNull(file.getFilename()), file);

            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        } catch (MessagingException e) {

            return "Error while sending mail!!!";
        }
    }

    public String sendHtmlMail(EmailDetails details) {
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody(), true);
            mimeMessageHelper.setSubject(
                    details.getSubject());

            javaMailSender.send(mimeMessage);
            return "Mail Sent Successfully...";
        } catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    public EmailDetails buildEmail(String email, String name, String link) {
        String message = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Confirm your sign up</title>\n" +
                "    <style>\n" +
                "        .emailWarp {\n" +
                "          height: 100%;\n" +
                "          font-size: 14px;\n" +
                "          color: #212121;\n" +
                "          display: flex;\n" +
                "          justify-content: center;\n" +
                "          overflow: auto;\n" +
                "          margin: 0;\n" +
                "          padding: 0;\n" +
                "        }\n" +
                "    \n" +
                "        h1.emailTitle {\n" +
                "          font-size: 26px;\n" +
                "          font-weight: 500;\n" +
                "          margin-bottom: 15px;\n" +
                "          color: #252525;\n" +
                "        }\n" +
                "    \n" +
                "        a.linksBtn {\n" +
                "          border: 0;\n" +
                "          background: #b4d9ef;\n" +
                "          color: #fff;\n" +
                "          width: 100%;\n" +
                "          height: 50px;\n" +
                "          line-height: 50px;\n" +
                "          font-size: 16px;\n" +
                "          margin: 40px auto;\n" +
                "          box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.15);\n" +
                "          border-radius: 4px;\n" +
                "          outline: none;\n" +
                "          cursor: pointer;\n" +
                "          text-align: center;\n" +
                "          display: block;\n" +
                "          text-decoration: none;\n" +
                "          transition: all 0.3s ease-in-out;\n" +
                "        }\n" +
                "\n" +
                "        a.linksBtn:hover {\n" +
                "            background: #59A2DA;\n" +
                "            transform: scale(1.2);\n" +
                "        }\n" +
                "    \n" +
                "        .warmTips {\n" +
                "          color: #757575;\n" +
                "          background: #f7f7f7;\n" +
                "          padding: 20px;\n" +
                "        }\n" +
                "    \n" +
                "        .emailFooter {\n" +
                "          margin-top: 2em;\n" +
                "        }\n" +
                "    \n" +
                "        #confirmEmail {\n" +
                "          max-width: 500px;\n" +
                "        }\n" +
                "\n" +
                "      </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <section class=\"emailWarp\">\n" +
                "        <div id=\"confirmEmail\">\n" +
                "    \n" +
                "          <h1 class=\"emailTitle\">\n" +
                "            Dear <span>" + name + "</span>:\n" +
                "          </h1>\n" +
                "          <p>Welcome to PowerPuff online bookshelf!</p>\n" +
                "          <p>To complete your registration, please click the <b> link </b>below to verify your email address:</p>\n" +
                "    \n" +
                "          <a href=\"" + link + "\">Activate Now</a>\n" +
                "    \n" +
                "          <div class=\"warmTips\">\n" +
                "            The above link is a one-time link and only valid for 24 hours. Please complete the operation as soon as possible.\n" +
                "          </div>\n" +
                "    \n" +
                "          <div class=\"emailFooter\">\n" +
                "            <p>Best regards</p>\n" +
                "            <b>PowerPuff</b>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </section>\n" +
                "</body>\n" +
                "</html>";
        String subject = "Confirm your email.";
        return new EmailDetails(email, message, subject, "");
    }
}
