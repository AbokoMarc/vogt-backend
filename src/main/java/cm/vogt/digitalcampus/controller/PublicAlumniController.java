package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.repository.AlumniRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/alumni")
@RequiredArgsConstructor
public class PublicAlumniController {

    private final AlumniRepository alumniRepository;

    @GetMapping("/success-stories")
    public ApiResponse<?> successStories() {
        return ApiResponse.ok(alumniRepository.findAll().stream()
                .filter(a -> a.getSuccessStory() != null && !a.getSuccessStory().isBlank())
                .toList());
    }
}
