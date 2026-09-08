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
@Table(name = "gallery_items")
public class GalleryItem extends BaseEntity {

    private String album; // CAMPUS, LABORATOIRES, ETUDIANTS, EVENEMENTS, PROJETS
    private String mediaUrl;
    private String mediaType; // IMAGE, VIDEO
    private String title;
    private String altText;
}
