package cm.vogt.digitalcampus.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "partners")
public class Partner extends BaseEntity {

    private String name;
    private String category; // ACADEMIQUE, ENTREPRISE, INSTITUTIONNEL, STARTUP
    private String logoUrl;
    private String websiteUrl;
    private boolean active = true;
}
