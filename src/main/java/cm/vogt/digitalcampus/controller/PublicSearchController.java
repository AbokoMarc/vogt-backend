package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import cm.vogt.digitalcampus.repository.FaqItemRepository;
import cm.vogt.digitalcampus.repository.NewsRepository;
import cm.vogt.digitalcampus.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recherche globale simple sur le contenu publie (formations, actualites, FAQ).
 * Filtrage en memoire — suffisant pour le volume de contenu d'un site d'ecole ;
 * a remplacer par une recherche full-text (Postgres tsvector ou Elasticsearch)
 * si le volume de contenu grossit significativement.
 */
@RestController
@RequestMapping("/api/v1/public/search")
@RequiredArgsConstructor
public class PublicSearchController {

    private final ProgramRepository programRepository;
    private final NewsRepository newsRepository;
    private final FaqItemRepository faqItemRepository;

    @GetMapping
    public ApiResponse<Map<String, Object>> search(@RequestParam String q) {
        String needle = q.toLowerCase();

        var programs = programRepository.findByStatusOrderByDisplayOrderAsc(PublicationStatus.PUBLISHED).stream()
                .filter(p -> contains(p.getName(), needle) || contains(p.getShortDescription(), needle))
                .limit(10).toList();

        var news = newsRepository.findByStatusOrderByPublishedAtDesc(PublicationStatus.PUBLISHED, PageRequest.of(0, 50))
                .getContent().stream()
                .filter(n -> contains(n.getTitle(), needle) || contains(n.getExcerpt(), needle))
                .limit(10).toList();

        var faq = faqItemRepository.findAll().stream()
                .filter(f -> contains(f.getQuestion(), needle) || contains(f.getAnswer(), needle))
                .limit(10).toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("programs", programs);
        result.put("news", news);
        result.put("faq", faq);
        return ApiResponse.ok(result);
    }

    private boolean contains(String haystack, String needle) {
        return haystack != null && haystack.toLowerCase().contains(needle);
    }
}
