package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.dto.program.ProgramResponse;
import cm.vogt.digitalcampus.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** /formations et /formations/{slug} — contenu 100% administrable, jamais hardcode. */
@RestController
@RequestMapping("/api/v1/public/programs")
@RequiredArgsConstructor
public class PublicProgramController {

    private final ProgramService programService;

    @GetMapping
    public ApiResponse<List<ProgramResponse>> list(@RequestParam(required = false) String category,
                                                     @RequestParam(defaultValue = "fr") String lang) {
        return ApiResponse.ok(programService.listPublished(category, lang));
    }

    @GetMapping("/{slug}")
    public ApiResponse<ProgramResponse> getBySlug(@PathVariable String slug, @RequestParam(defaultValue = "fr") String lang) {
        return ApiResponse.ok(programService.getPublishedBySlug(slug, lang));
    }
}
