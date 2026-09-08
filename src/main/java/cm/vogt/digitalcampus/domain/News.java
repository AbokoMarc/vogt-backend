package cm.vogt.digitalcampus.domain;

import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import jakarta.persistence.Column;
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
@Table(name = "news")
public class News extends BaseEntity {

    @Column(unique = true)
    private String slug;

    private String title;
    private String category; // CAMPUS, INNOVATION, RECHERCHE, ADMISSIONS, EVENEMENTS, VIE_ETUDIANTE
    private String coverImageUrl;
    private String excerpt;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String titleEn;
    private String excerptEn;

    @Column(columnDefinition = "TEXT")
    private String contentEn;

    private String author;
    private Instant publishedAt;
    private String seoTitle;
    private String metaDescription;

    @Enumerated(EnumType.STRING)
    private PublicationStatus status = PublicationStatus.DRAFT;
}
