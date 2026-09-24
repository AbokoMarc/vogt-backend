package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findByStudentId(UUID studentId);
    List<Attendance> findByCourseId(UUID courseId);
}
