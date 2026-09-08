package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.Student;
import cm.vogt.digitalcampus.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/students")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ADMISSIONS_OFFICER','ACADEMIC_ADMIN')")
public class AdminStudentController {

    private final StudentRepository studentRepository;

    @GetMapping
    public ApiResponse<List<Student>> list() {
        return ApiResponse.ok(studentRepository.findAll());
    }
}
