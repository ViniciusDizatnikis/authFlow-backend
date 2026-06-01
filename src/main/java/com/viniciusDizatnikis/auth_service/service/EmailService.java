package com.viniciusDizatnikis.auth_service.service;

import com.viniciusDizatnikis.auth_service.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    public void sendVerificationCode(String toEmail, String code) {

        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("expirationMinutes", 30);

        String html = templateEngine.process(
                "email/verification-email",
                context
        );

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject("Verificação de E-mail");
            helper.setText(html, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailSendingException(
                    "Falha ao montar o e-mail de verificação.",
                    e
            );
        } catch (Exception e) {
            throw new EmailSendingException(
                    "Falha ao enviar o e-mail de verificação.",
                    e
            );
        }
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