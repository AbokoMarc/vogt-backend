package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/partners")
@RequiredArgsConstructor
public class PublicPartnerController {

    private final PartnerRepository partnerRepository;

    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.ok(partnerRepository.findAll().stream().filter(p -> p.isActive()).toList());
    }
}
