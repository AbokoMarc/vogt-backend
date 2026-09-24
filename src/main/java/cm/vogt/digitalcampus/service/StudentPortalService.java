package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.domain.Student;
import cm.vogt.digitalcampus.dto.student.GradeResponse;
import cm.vogt.digitalcampus.dto.student.StudentProfileResponse;
import cm.vogt.digitalcampus.dto.student.TimetableSlotResponse;
import cm.vogt.digitalcampus.repository.GradeRepository;
import cm.vogt.digitalcampus.repository.StudentRepository;
import cm.vogt.digitalcampus.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentPortalService {

    private final StudentRepository studentRepository;
    private final TimetableRepository timetableRepository;
    private final GradeRepository gradeRepository;

    public StudentProfileResponse profile(UUID userId) {
        Student student = getByUserId(userId);
        return new StudentProfileResponse(
                student.getMatricule(),
                student.getUser().getFirstName(),
                student.getUser().getLastName(),
                student.getProgram() != null ? student.getProgram().getName() : null,
                student.getSpecialization() != null ? student.getSpecialization().getName() : null,
                student.getYearOfStudy()
        );
    }

    public List<TimetableSlotResponse> schedule(UUID userId) {
        Student student = getByUserId(userId);
        if (student.getProgram() == null) return List.of();
        return timetableRepository.findByProgramIdAndYearOfStudy(student.getProgram().getId(), student.getYearOfStudy())
                .stream()
                .map(t -> new TimetableSlotResponse(
                        t.getCourse() != null ? t.getCourse().getName() : null,
                        t.getTeacher() != null ? (t.getTeacher().getUser().getFirstName() + " " + t.getTeacher().getUser().getLastName()) : null,
                        t.getDayOfWeek() != null ? t.getDayOfWeek().name() : null,
                        t.getStartTime() != null ? t.getStartTime().toString() : null,
                        t.getEndTime() != null ? t.getEndTime().toString() : null,
                        t.getRoom()
                )).toList();
    }

    public List<GradeResponse> grades(UUID userId, String semester) {
        Student student = getByUserId(userId);
        var grades = (semester == null || semester.isBlank())
                ? gradeRepository.findByStudentId(student.getId())
                : gradeRepository.findByStudentIdAndSemester(student.getId(), semester);
        return grades.stream()
                .map(g -> new GradeResponse(g.getCourse() != null ? g.getCourse().getName() : null, g.getSemester(), g.getScore()))
                .toList();
    }

    private Student getByUserId(UUID userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil etudiant introuvable."));
    }
}
