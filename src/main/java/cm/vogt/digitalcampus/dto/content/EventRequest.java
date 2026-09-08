package cm.vogt.digitalcampus.dto.content;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class EventRequest {
    @NotBlank private String title;
    private String description;
    private String titleEn;
    private String descriptionEn;
    private String location;
    private Instant startsAt;
    private Instant endsAt;
    private String coverImageUrl;
    private boolean registrationRequired;
    private String registrationUrl;
}
