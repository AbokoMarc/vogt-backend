package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByMatricule(String matricule);
    Optional<Student> findByUserId(UUID userId);
}
