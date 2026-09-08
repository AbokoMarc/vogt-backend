package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.Attendance;
import cm.vogt.digitalcampus.repository.AttendanceRepository;
import cm.vogt.digitalcampus.repository.StudentRepository;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Presences — saisie par l'enseignant, consultation par l'etudiant concerne. */
@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @PostMapping("/api/v1/teachers/me/attendance")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Attendance> record(@RequestBody Attendance attendance) {
        return ApiResponse.ok("Presence enregistree.", attendanceRepository.save(attendance));
    }

    @GetMapping("/api/v1/students/me/attendance")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> myAttendance(@AuthenticationPrincipal VogtUserDetails principal) {
        var student = studentRepository.findByUserId(principal.getUser().getId()).orElseThrow();
        return ApiResponse.ok(attendanceRepository.findByStudentId(student.getId()));
    }
}
