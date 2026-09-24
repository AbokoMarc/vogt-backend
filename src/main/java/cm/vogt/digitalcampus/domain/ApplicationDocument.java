package cm.vogt.digitalcampus.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Piece jointe candidature — fichier stocke en object storage (jamais en base). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "application_documents")
public class ApplicationDocument extends BaseEntity {

    private String documentType; // ACTE_NAISSANCE, DIPLOME, CNI, PHOTO, AUTRE
    private String fileUrl;
    private String originalFileName;
    private boolean verified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;
}
