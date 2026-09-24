package cm.vogt.digitalcampus.domain;

import cm.vogt.digitalcampus.common.enums.AcademicYearStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entite centrale du "zero hardcode" : chaque annee academique porte ses
 * propres formations, frais, dates de concours, calendrier et documents.
 * Une seule annee est ACTIVE a la fois ; les precedentes sont ARCHIVED.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "academic_years")
public class AcademicYear extends BaseEntity {

    /** Ex. "2026-2027" */
    private String label;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private AcademicYearStatus status;
}
