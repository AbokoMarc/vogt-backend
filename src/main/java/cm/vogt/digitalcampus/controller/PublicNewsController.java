package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.common.PageResponse;
import cm.vogt.digitalcampus.domain.News;
import cm.vogt.digitalcampus.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/news")
@RequiredArgsConstructor
public class PublicNewsController {

    private final NewsService newsService;

    @GetMapping
    public ApiResponse<PageResponse<News>> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "fr") String lang) {
        return ApiResponse.ok(newsService.listPublished(category, PageRequest.of(page, size), lang));
    }

    @GetMapping("/{slug}")
    public ApiResponse<News> getBySlug(@PathVariable String slug, @RequestParam(defaultValue = "fr") String lang) {
        return ApiResponse.ok(newsService.getPublishedBySlug(slug, lang));
    }
}
