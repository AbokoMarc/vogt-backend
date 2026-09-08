package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.repository.FaqItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/faq")
@RequiredArgsConstructor
public class PublicFaqController {

    private final FaqItemRepository faqItemRepository;

    @GetMapping
    public ApiResponse<?> list(@RequestParam(required = false) String category,
                                @RequestParam(defaultValue = "fr") String lang) {
        var all = faqItemRepository.findAll();
        if (category != null && !category.isBlank()) {
            all = all.stream().filter(f -> category.equalsIgnoreCase(f.getCategory())).toList();
        }
        if ("en".equalsIgnoreCase(lang)) {
            all.forEach(f -> {
                if (f.getQuestionEn() != null && !f.getQuestionEn().isBlank()) f.setQuestion(f.getQuestionEn());
                if (f.getAnswerEn() != null && !f.getAnswerEn().isBlank()) f.setAnswer(f.getAnswerEn());
            });
        }
        return ApiResponse.ok(all);
    }
}
