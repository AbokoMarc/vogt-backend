package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/** Upload generique de media (galerie, logos partenaires, couvertures d'actualites...). */
@RestController
@RequestMapping("/api/v1/admin/media")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','COMMUNICATION_ADMIN','ACADEMIC_ADMIN')")
public class AdminMediaUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> upload(@RequestParam MultipartFile file,
                                                     @RequestParam(defaultValue = "media") String folder) throws IOException {
        String url = fileStorageService.store(file, folder);
        return ApiResponse.ok(Map.of("url", url));
    }
}
