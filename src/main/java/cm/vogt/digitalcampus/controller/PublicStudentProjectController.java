package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.repository.StudentProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/projects")
@RequiredArgsConstructor
public class PublicStudentProjectController {

    private final StudentProjectRepository studentProjectRepository;

    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.ok(studentProjectRepository.findAll());
    }
}
