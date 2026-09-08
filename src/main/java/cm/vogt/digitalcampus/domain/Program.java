package cm.vogt.digitalcampus.domain;

import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Formation d'ingenieur (ex. Genie logiciel, IA, Data Science, Electronique, Robotique).
 * Publiee sur /formations/{slug} — le contenu est entierement administrable.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "programs")
public class Program extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    /** Domaine de filtre : INFORMATIQUE, IA_DATA, ELECTRONIQUE, ROBOTIQUE ... administrable. */
    private String category;

    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String fullDescription;

    /** Traduction anglaise optionnelle — si absente, le contenu FR est utilise en repli. */
    private String nameEn;
    private String shortDescriptionEn;

    @Column(columnDefinition = "TEXT")
    private String fullDescriptionEn;

    private String heroImageUrl;
    private String durationLabel;   // ex. "5 ans"
    private String diplomaLabel;    // ex. "Diplome d'Ingenieur"
    private String level;           // ex. "Ingenieur"

    @ElementCollection
    @CollectionTable(name = "program_tags", joinColumns = @JoinColumn(name = "program_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;

    /** Montant des frais — jamais hardcode cote frontend, toujours lu depuis l'API. */
    private Long tuitionAmountXaf;

    @Enumerated(EnumType.STRING)
    private PublicationStatus status = PublicationStatus.DRAFT;

    private int displayOrder = 0;
}
