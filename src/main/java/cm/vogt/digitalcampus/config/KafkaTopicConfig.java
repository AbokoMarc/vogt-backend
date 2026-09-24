package cm.vogt.digitalcampus.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Topics Kafka du Digital Campus — decouple les notifications (email/push/WhatsApp)
 * du reste de l'application, dans la meme logique que TransCam/Fintech-CEMAC.
 */
@Configuration
public class KafkaTopicConfig {

    public static final String APPLICATION_STATUS_CHANGED = "vogt.application.status-changed";
    public static final String NOTIFICATION_REQUESTED = "vogt.notification.requested";
    public static final String CONTENT_PUBLISHED = "vogt.content.published";

    @Bean
    public NewTopic applicationStatusChangedTopic() {
        return TopicBuilder.name(APPLICATION_STATUS_CHANGED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic notificationRequestedTopic() {
        return TopicBuilder.name(NOTIFICATION_REQUESTED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic contentPublishedTopic() {
        return TopicBuilder.name(CONTENT_PUBLISHED).partitions(1).replicas(1).build();
    }
}
