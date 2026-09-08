package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<Grade, UUID> {
    List<Grade> findByStudentId(UUID studentId);
    List<Grade> findByStudentIdAndSemester(UUID studentId, String semester);
    List<Grade> findByCourseId(UUID courseId);
}
