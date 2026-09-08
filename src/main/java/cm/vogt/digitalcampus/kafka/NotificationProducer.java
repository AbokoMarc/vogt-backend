package cm.vogt.digitalcampus.kafka;

import cm.vogt.digitalcampus.config.KafkaTopicConfig;
import cm.vogt.digitalcampus.kafka.event.ApplicationStatusChangedEvent;
import cm.vogt.digitalcampus.kafka.event.NotificationRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.APPLICATION_STATUS_CHANGED, event.getTrackingNumber(), event);
    }

    public void requestNotification(NotificationRequestedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.NOTIFICATION_REQUESTED, event.getRecipientEmail(), event);
    }
}
