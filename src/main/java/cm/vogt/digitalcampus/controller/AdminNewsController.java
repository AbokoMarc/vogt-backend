package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.News;
import cm.vogt.digitalcampus.dto.content.NewsRequest;
import cm.vogt.digitalcampus.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

/** VOGT ADMIN — editeur d'article (COMMUNICATION_ADMIN). */
@RestController
@RequestMapping("/api/v1/admin/news")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','COMMUNICATION_ADMIN')")
public class AdminNewsController {

    private final NewsService newsService;

    @GetMapping
    public ApiResponse<java.util.List<News>> listAll() {
        return ApiResponse.ok(newsService.listAllForAdmin());
    }

    @PostMapping
    public ApiResponse<News> create(@Valid @RequestBody NewsRequest request) {
        return ApiResponse.ok("Brouillon cree.", newsService.create(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<News> update(@PathVariable UUID id, @Valid @RequestBody NewsRequest request) {
        return ApiResponse.ok("Article mis a jour.", newsService.update(id, request));
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable UUID id) {
        newsService.publish(id);
        return ApiResponse.ok("Article publie.", null);
    }

    @PostMapping("/{id}/schedule")
    public ApiResponse<Void> schedule(@PathVariable UUID id, @RequestParam Instant publishAt) {
        newsService.schedule(id, publishAt);
        return ApiResponse.ok("Article programme.", null);
    }

    @PostMapping("/{id}/archive")
    public ApiResponse<Void> archive(@PathVariable UUID id) {
        newsService.archive(id);
        return ApiResponse.ok("Article archive.", null);
    }
}
