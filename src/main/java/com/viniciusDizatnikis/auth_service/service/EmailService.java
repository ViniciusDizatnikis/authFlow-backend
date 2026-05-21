package com.viniciusDizatnikis.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("Verificação de email");
        message.setText(
                "Seu código de verificação é: " + code + "\n\n" +
                        "O código expira em 30 minutos.\n" +
                        "Se você não criou uma conta, ignore este email."
        );
        mailSender.send(message);
    }

    public void sendPasswordResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("Redefinição de senha");
        message.setText(
                "Seu código para redefinir a senha é: " + code + "\n\n" +
                        "O código expira em 10 minutos.\n" +
                        "Se você não solicitou isso, ignore este email."
        );
        mailSender.send(message);
    }
}