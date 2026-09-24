package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import cm.vogt.digitalcampus.domain.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgramRepository extends JpaRepository<Program, UUID> {
    Optional<Program> findBySlugAndStatus(String slug, PublicationStatus status);
    List<Program> findByStatusOrderByDisplayOrderAsc(PublicationStatus status);
    List<Program> findByStatusAndCategoryOrderByDisplayOrderAsc(PublicationStatus status, String category);
    boolean existsBySlug(String slug);
}
