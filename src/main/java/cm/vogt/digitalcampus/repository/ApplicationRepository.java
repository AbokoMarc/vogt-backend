package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Optional<Application> findByTrackingNumber(String trackingNumber);
    long countByAcademicYearId(UUID academicYearId);
    Optional<Application> findFirstByCandidateIdOrderByCreatedAtDesc(UUID candidateId);
}
