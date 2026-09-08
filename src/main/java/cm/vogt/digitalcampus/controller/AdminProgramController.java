package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.dto.program.ProgramRequest;
import cm.vogt.digitalcampus.dto.program.ProgramResponse;
import cm.vogt.digitalcampus.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** VOGT ADMIN — CRUD des formations. Reserve a ACADEMIC_ADMIN et administrateurs. */
@RestController
@RequestMapping("/api/v1/admin/programs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ACADEMIC_ADMIN')")
public class AdminProgramController {

    private final ProgramService programService;

    @GetMapping
    public ApiResponse<java.util.List<ProgramResponse>> listAll() {
        return ApiResponse.ok(programService.listAllForAdmin());
    }

    @PostMapping
    public ApiResponse<ProgramResponse> create(@Valid @RequestBody ProgramRequest request) {
        return ApiResponse.ok("Formation creee.", programService.create(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<ProgramResponse> update(@PathVariable UUID id, @Valid @RequestBody ProgramRequest request) {
        return ApiResponse.ok("Formation mise a jour.", programService.update(id, request));
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable UUID id) {
        programService.publish(id);
        return ApiResponse.ok("Formation publiee.", null);
    }

    @PostMapping("/{id}/unpublish")
    public ApiResponse<Void> unpublish(@PathVariable UUID id) {
        programService.unpublish(id);
        return ApiResponse.ok("Formation depubliee.", null);
    }

    @PostMapping("/{id}/archive")
    public ApiResponse<Void> archive(@PathVariable UUID id) {
        programService.archive(id);
        return ApiResponse.ok("Formation archivee.", null);
    }
}
