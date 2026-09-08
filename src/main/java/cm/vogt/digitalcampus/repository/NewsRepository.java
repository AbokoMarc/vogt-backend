package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import cm.vogt.digitalcampus.domain.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<News, UUID> {
    Optional<News> findBySlugAndStatus(String slug, PublicationStatus status);
    Page<News> findByStatusOrderByPublishedAtDesc(PublicationStatus status, Pageable pageable);
    Page<News> findByStatusAndCategoryOrderByPublishedAtDesc(PublicationStatus status, String category, Pageable pageable);
}
