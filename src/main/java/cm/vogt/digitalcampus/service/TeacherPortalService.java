package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.domain.Course;
import cm.vogt.digitalcampus.domain.Grade;
import cm.vogt.digitalcampus.domain.Student;
import cm.vogt.digitalcampus.domain.Teacher;
import cm.vogt.digitalcampus.dto.student.GradeResponse;
import cm.vogt.digitalcampus.dto.teacher.GradeEntryRequest;
import cm.vogt.digitalcampus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherPortalService {

    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final TimetableRepository timetableRepository;

    public Teacher getByUserId(UUID userId) {
        return teacherRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Profil enseignant introuvable."));
    }

    public Object myCourses(UUID userId) {
        Teacher teacher = getByUserId(userId);
        return timetableRepository.findByTeacherId(teacher.getId());
    }

    public cm.vogt.digitalcampus.dto.teacher.TeacherProfileResponse profile(UUID userId) {
        Teacher teacher = getByUserId(userId);
        return new cm.vogt.digitalcampus.dto.teacher.TeacherProfileResponse(
                teacher.getUser().getFirstName(), teacher.getUser().getLastName(),
                teacher.getDepartment(), teacher.getTitle()
        );
    }

    /** Etudiants inscrits aux formations que cet enseignant enseigne (via son emploi du temps) —
     *  evite de demander un UUID d'etudiant saisi a la main pour noter/pointer. */
    public List<cm.vogt.digitalcampus.dto.teacher.StudentBriefResponse> myStudents(UUID userId) {
        Teacher teacher = getByUserId(userId);
        var programIds = timetableRepository.findByTeacherId(teacher.getId()).stream()
                .map(t -> t.getProgram() != null ? t.getProgram().getId() : null)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        return studentRepository.findAll().stream()
                .filter(s -> s.getProgram() != null && programIds.contains(s.getProgram().getId()))
                .map(s -> new cm.vogt.digitalcampus.dto.teacher.StudentBriefResponse(
                        s.getId(), s.getMatricule(), s.getUser().getFirstName(), s.getUser().getLastName(),
                        s.getProgram().getName()
                ))
                .toList();
    }

    /** Matieres distinctes enseignees par ce professeur (deduites de son emploi du temps). */
    public List<java.util.Map<String, String>> myCourseOptions(UUID userId) {
        Teacher teacher = getByUserId(userId);
        return timetableRepository.findByTeacherId(teacher.getId()).stream()
                .filter(t -> t.getCourse() != null)
                .map(t -> java.util.Map.of("id", t.getCourse().getId().toString(), "name", t.getCourse().getName()))
                .distinct()
                .toList();
    }

    @Transactional
    public GradeResponse enterGrade(UUID teacherUserId, GradeEntryRequest request) {
        Teacher teacher = getByUserId(teacherUserId);
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException("Etudiant introuvable."));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Matiere introuvable."));

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setCourse(course);
        grade.setSemester(request.getSemester());
        grade.setScore(request.getScore());
        grade.setEnteredByTeacherId(teacher.getId().toString());
        gradeRepository.save(grade);

        return new GradeResponse(course.getName(), request.getSemester(), request.getScore());
    }

    public List<Grade> courseGrades(UUID courseId) {
        return gradeRepository.findByCourseId(courseId);
    }
}
