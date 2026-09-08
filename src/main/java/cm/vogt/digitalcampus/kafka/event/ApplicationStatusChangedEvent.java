package cm.vogt.digitalcampus.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusChangedEvent {
    private String trackingNumber;
    private String candidateEmail;
    private String previousStatus;
    private String newStatus;
}
