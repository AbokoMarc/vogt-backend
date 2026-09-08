package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.domain.Lab;
import cm.vogt.digitalcampus.repository.LabRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Vogt Labs — modules activables/desactivables depuis le CMS (section 12 du cahier des charges). */
@RestController
@RequestMapping("/api/v1/admin/labs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ACADEMIC_ADMIN')")
public class AdminLabController {

    private final LabRepository labRepository;

    @GetMapping
    public ApiResponse<List<Lab>> list() { return ApiResponse.ok(labRepository.findAll()); }

    @PostMapping
    public ApiResponse<Lab> create(@RequestBody Lab lab) { return ApiResponse.ok("Laboratoire cree.", labRepository.save(lab)); }

    @PatchMapping("/{id}")
    public ApiResponse<Lab> update(@PathVariable UUID id, @RequestBody Lab payload) {
        Lab lab = labRepository.findById(id).orElseThrow(() -> new NotFoundException("Laboratoire introuvable."));
        lab.setName(payload.getName());
        lab.setDescription(payload.getDescription());
        lab.setIconKey(payload.getIconKey());
        lab.setFutureProject(payload.isFutureProject());
        return ApiResponse.ok("Laboratoire mis a jour.", labRepository.save(lab));
    }

    @PostMapping("/{id}/toggle")
    public ApiResponse<Lab> toggle(@PathVariable UUID id) {
        Lab lab = labRepository.findById(id).orElseThrow(() -> new NotFoundException("Laboratoire introuvable."));
        lab.setActive(!lab.isActive());
        return ApiResponse.ok("Statut du laboratoire bascule.", labRepository.save(lab));
    }
}
