package cm.vogt.digitalcampus.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vogt Labs — module activable/desactivable en CMS. Si un laboratoire n'existe
 * pas encore physiquement, isFutureProject=true l'affiche comme "axe d'innovation".
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "labs")
public class Lab extends BaseEntity {

    private String name;
    private String description;
    private String iconKey;
    private String imageUrl;
    private boolean active = true;
    private boolean futureProject = true;
}
