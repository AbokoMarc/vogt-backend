package cm.vogt.digitalcampus.domain;

import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event extends BaseEntity {

    private String title;
    private String description;
    private String titleEn;
    private String descriptionEn;
    private String location;
    private Instant startsAt;
    private Instant endsAt;
    private String coverImageUrl;
    private boolean registrationRequired = false;
    private String registrationUrl;

    @Enumerated(EnumType.STRING)
    private PublicationStatus status = PublicationStatus.DRAFT;
}
