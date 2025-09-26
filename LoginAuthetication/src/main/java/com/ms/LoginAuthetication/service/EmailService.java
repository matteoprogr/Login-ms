package com.ms.LoginAuthetication.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Value("${app.confirmationUrl}")
    private String confirmationUrl;

    @Value("${mail.username}")
    private String usernameMail;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String key){
        try{
        String subject = "Conferma la tua registrazione";
        String url = confirmationUrl + key;
        String message = "Ciao!\n\n" +
                "Grazie per esserti registrato. Per completare la registrazione, " +
                "conferma la tua email cliccando sul link qui sotto:\n\n" +
                url + "\n\n" +
                "Se non hai richiesto tu questa registrazione, puoi ignorare questo messaggio.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(usernameMail);

        mailSender.send(email);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
