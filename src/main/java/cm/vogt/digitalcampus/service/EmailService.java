package cm.vogt.digitalcampus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envoi d'email reel via SMTP. Desactive par defaut (app.mail.enabled=false) —
 * tant que MAIL_HOST/MAIL_USERNAME/MAIL_PASSWORD ne sont pas renseignes en
 * production, les envois sont simplement journalises au lieu d'echouer.
 * Fonctionne avec n'importe quel fournisseur SMTP (Gmail, SendGrid, Mailgun,
 * SMTP institutionnel de l'INUCASTY...).
 */
@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean enabled;

    @Value("${app.mail.from-address}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String to, String subject, String body) {
        if (!enabled) {
            log.info("[EMAIL desactive] A: {} | Sujet: {} | Corps: {}", to, subject, body);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Echec d'envoi d'email a {} : {}", to, e.getMessage());
        }
    }
}
