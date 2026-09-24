package cm.vogt.digitalcampus.kafka;

import cm.vogt.digitalcampus.config.KafkaTopicConfig;
import cm.vogt.digitalcampus.domain.Notification;
import cm.vogt.digitalcampus.domain.User;
import cm.vogt.digitalcampus.kafka.event.ApplicationStatusChangedEvent;
import cm.vogt.digitalcampus.kafka.event.NotificationRequestedEvent;
import cm.vogt.digitalcampus.repository.NotificationRepository;
import cm.vogt.digitalcampus.repository.UserRepository;
import cm.vogt.digitalcampus.service.EmailService;
import cm.vogt.digitalcampus.service.PushNotificationService;
import cm.vogt.digitalcampus.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Ecoute les evenements internes et cree les notifications visibles dans les
 * portails candidat/etudiant. Le veritable envoi email/WhatsApp est branche ici
 * (adaptateur a completer selon le fournisseur retenu par l'etablissement).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PushNotificationService pushNotificationService;
    private final WhatsAppService whatsAppService;

    @KafkaListener(topics = KafkaTopicConfig.APPLICATION_STATUS_CHANGED, groupId = "vogt-digital-campus")
    public void onApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        userRepository.findByEmailIgnoreCase(event.getCandidateEmail()).ifPresent(user -> {
            String title = "Mise a jour de votre candidature " + event.getTrackingNumber();
            String message = "Statut : " + event.getPreviousStatus() + " -> " + event.getNewStatus();

            Notification notification = new Notification();
            notification.setRecipient(user);
            notification.setTitle(title);
            notification.setMessage(message);
            notificationRepository.save(notification);

            emailService.send(
                    user.getEmail(),
                    title,
                    "Bonjour " + user.getFirstName() + ",\n\nLe statut de votre candidature " + event.getTrackingNumber() +
                            " est passe de " + event.getPreviousStatus() + " a " + event.getNewStatus() +
                            ".\n\nVous pouvez suivre votre dossier depuis votre espace candidat.\n\nVOGT HIGH TECH — Une ecole de l'INUCASTY"
            );

            pushNotificationService.sendToUser(user, title, message);
            if (user.getPhone() != null && !user.getPhone().isBlank()) {
                whatsAppService.send(user.getPhone(), title + " — " + message);
            }
        });
        log.info("Candidature {} : {} -> {}", event.getTrackingNumber(), event.getPreviousStatus(), event.getNewStatus());
    }

    @KafkaListener(topics = KafkaTopicConfig.NOTIFICATION_REQUESTED, groupId = "vogt-digital-campus")
    public void onNotificationRequested(NotificationRequestedEvent event) {
        if ("EMAIL".equalsIgnoreCase(event.getChannel())) {
            emailService.send(event.getRecipientEmail(), event.getTitle(), event.getMessage());
        } else if ("WHATSAPP".equalsIgnoreCase(event.getChannel())) {
            userRepository.findByEmailIgnoreCase(event.getRecipientEmail())
                    .ifPresent(u -> whatsAppService.send(u.getPhone(), event.getTitle() + " — " + event.getMessage()));
        } else {
            log.info("Notification [{}] pour {} : {}", event.getChannel(), event.getRecipientEmail(), event.getTitle());
        }
    }
}
