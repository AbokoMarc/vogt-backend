package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.domain.Partner;
import cm.vogt.digitalcampus.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/partners")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','COMMUNICATION_ADMIN')")
public class AdminPartnerController {

    private final PartnerRepository partnerRepository;

    @GetMapping
    public ApiResponse<List<Partner>> list() { return ApiResponse.ok(partnerRepository.findAll()); }

    @PostMapping
    public ApiResponse<Partner> create(@RequestBody Partner partner) { return ApiResponse.ok("Partenaire cree.", partnerRepository.save(partner)); }

    @PatchMapping("/{id}")
    public ApiResponse<Partner> update(@PathVariable UUID id, @RequestBody Partner payload) {
        Partner p = partnerRepository.findById(id).orElseThrow(() -> new NotFoundException("Partenaire introuvable."));
        p.setName(payload.getName());
        p.setCategory(payload.getCategory());
        p.setLogoUrl(payload.getLogoUrl());
        p.setWebsiteUrl(payload.getWebsiteUrl());
        p.setActive(payload.isActive());
        return ApiResponse.ok("Partenaire mis a jour.", partnerRepository.save(p));
    }
}
