package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.dto.application.ApplicationTrackerResponse;
import cm.vogt.digitalcampus.dto.application.UpdateApplicationStatusRequest;
import cm.vogt.digitalcampus.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Utilise par le bureau des admissions (ADMISSIONS_OFFICER) pour faire avancer un dossier. */
@RestController
@RequestMapping("/api/v1/admin/applications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ADMISSIONS_OFFICER')")
public class AdminApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    public ApiResponse<java.util.List<cm.vogt.digitalcampus.dto.admin.ApplicationAdminResponse>> listAll() {
        return ApiResponse.ok(applicationService.listAllForAdmin());
    }

    @PatchMapping("/{trackingNumber}/status")
    public ApiResponse<ApplicationTrackerResponse> updateStatus(
            @PathVariable String trackingNumber,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        return ApiResponse.ok("Statut mis a jour.", applicationService.updateStatus(trackingNumber, request));
    }
}
