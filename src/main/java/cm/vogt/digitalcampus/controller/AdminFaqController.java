package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.FaqItem;
import cm.vogt.digitalcampus.repository.FaqItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/faq")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ADMISSIONS_OFFICER','ACADEMIC_ADMIN')")
public class AdminFaqController {

    private final FaqItemRepository faqItemRepository;

    @GetMapping
    public ApiResponse<List<FaqItem>> list() {
        return ApiResponse.ok(faqItemRepository.findAll());
    }

    @PostMapping
    public ApiResponse<FaqItem> create(@RequestBody FaqItem item) { return ApiResponse.ok("FAQ ajoutee.", faqItemRepository.save(item)); }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) { faqItemRepository.deleteById(id); return ApiResponse.ok("FAQ supprimee.", null); }
}
