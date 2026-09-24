package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GalleryItemRepository extends JpaRepository<GalleryItem, UUID>, JpaSpecificationExecutor<GalleryItem> {

}
