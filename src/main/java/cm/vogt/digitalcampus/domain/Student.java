package cm.vogt.digitalcampus.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cree automatiquement lorsqu'une Application passe au statut ENROLLED. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student extends BaseEntity {

    @OneToOne
    private User user;

    private String matricule;
    private int yearOfStudy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;
}
