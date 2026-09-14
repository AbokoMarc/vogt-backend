package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.StudentProject;
import cm.vogt.digitalcampus.repository.StudentProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/projects")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ACADEMIC_ADMIN','COMMUNICATION_ADMIN')")
public class AdminStudentProjectController {

    private final StudentProjectRepository studentProjectRepository;

    @PostMapping
    public ApiResponse<StudentProject> create(@RequestBody StudentProject project) {
        return ApiResponse.ok("Projet ajoute.", studentProjectRepository.save(project));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        studentProjectRepository.deleteById(id);
        return ApiResponse.ok("Projet supprime.", null);
    }
}
