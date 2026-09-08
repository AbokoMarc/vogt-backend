package cm.vogt.digitalcampus.domain;

import cm.vogt.digitalcampus.common.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Candidature — identifiant unique genere au format VHT-{annee}-{sequence}.
 * Le statut pilote l'affichage du "suivi de candidature" cote portail candidat.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "applications")
public class Application extends BaseEntity {

    @Column
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_choice_program_id")
    private Program firstChoiceProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_choice_program_id")
    private Program secondChoiceProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    private String reviewerNote;
}
