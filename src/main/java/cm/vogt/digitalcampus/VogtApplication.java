package cm.vogt.digitalcampus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootApplication(exclude = { KafkaAutoConfiguration.class }) // Ajoute l'exclusion ici
public class VogtApplication {
    public static void main(String[] args) {
        SpringApplication.run(VogtApplication.class, args);
    }
}
