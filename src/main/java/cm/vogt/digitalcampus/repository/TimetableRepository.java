package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TimetableRepository extends JpaRepository<Timetable, UUID> {
    List<Timetable> findByProgramIdAndYearOfStudy(UUID programId, int yearOfStudy);
    List<Timetable> findByTeacherId(UUID teacherId);
}
