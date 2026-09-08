package cm.vogt.digitalcampus.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher extends BaseEntity {

    @OneToOne
    private User user;

    private String department;
    private String title; // ex. "Enseignant-chercheur"
    private String bio;

    @ElementCollection
    @CollectionTable(name = "teacher_specialties", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "specialty")
    private List<String> specialties = new ArrayList<>();
}
