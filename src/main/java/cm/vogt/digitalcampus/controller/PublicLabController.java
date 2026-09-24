package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.repository.LabRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/labs")
@RequiredArgsConstructor
public class PublicLabController {

    private final LabRepository labRepository;

    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.ok(labRepository.findAll().stream().filter(l -> l.isActive()).toList());
    }
}
