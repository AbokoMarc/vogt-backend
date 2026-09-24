package cm.vogt.digitalcampus.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Projet etudiant mis en avant sur la homepage / page Innovation. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student_projects")
public class StudentProject extends BaseEntity {

    private String title;
    private String category; // IA, ROBOTIQUE, SOFTWARE, IOT, DATA
    private String description;
    private String coverImageUrl;
    private String year;
    private String teamNames;

    @ElementCollection
    @CollectionTable(name = "student_project_tech", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "technology")
    private List<String> technologies = new ArrayList<>();
}
