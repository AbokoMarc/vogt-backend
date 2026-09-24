package cm.vogt.digitalcampus.kafka;

import cm.vogt.digitalcampus.config.KafkaTopicConfig;
import cm.vogt.digitalcampus.kafka.event.ApplicationStatusChangedEvent;
import cm.vogt.digitalcampus.kafka.event.NotificationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public interface NotificationProducer {
    void publishApplicationStatusChanged(ApplicationStatusChangedEvent event);
    void requestNotification(NotificationRequestedEvent event);
}

@Slf4j
@Service
@Profile("!prod")
@RequiredArgsConstructor
class KafkaNotificationProducer implements NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.APPLICATION_STATUS_CHANGED, event.getTrackingNumber(), event);
    }

    @Override
    public void requestNotification(NotificationRequestedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.NOTIFICATION_REQUESTED, event.getRecipientEmail(), event);
    }
}

@Slf4j
@Service
@Profile("prod")
class ProdNotificationProducer implements NotificationProducer {

    @Override
    public void publishApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        log.info("[PROD - MOCK KAFKA] Notification de changement de statut pour la candidature {}", event.getTrackingNumber());
    }

    @Override
    public void requestNotification(NotificationRequestedEvent event) {
        log.info("[PROD - MOCK KAFKA] Requête de notification reçue pour l'e-mail {}", event.getRecipientEmail());
    }
}
