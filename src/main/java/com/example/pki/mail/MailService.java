package com.example.pki.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Service
public class MailService<T> {
    private Environment env;
    private JavaMailSender javaMailSender;

    @Autowired
    public MailService(Environment env, JavaMailSender javaMailSender) {
        this.env = env;
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendMail(String recipient, T params, MailFormatter<T> mailFormatter) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mail = new MimeMessageHelper(message);
        mail.setTo(recipient);
        mail.setFrom(Objects.requireNonNull(this.env.getProperty("spring.mail.username")));

        mail.setSubject(mailFormatter.getSubject());
        mail.setText(mailFormatter.getText(params), true);

        javaMailSender.send(message);
    }
}
