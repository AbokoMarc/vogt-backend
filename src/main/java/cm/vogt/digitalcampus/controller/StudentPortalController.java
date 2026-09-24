package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import cm.vogt.digitalcampus.service.StudentPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Portail etudiant — chaque etudiant ne voit que ses propres donnees. */
@RestController
@RequestMapping("/api/v1/students/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentPortalController {

    private final StudentPortalService studentPortalService;

    @GetMapping("/profile")
    public ApiResponse<?> profile(@AuthenticationPrincipal VogtUserDetails principal) {
        return ApiResponse.ok(studentPortalService.profile(principal.getUser().getId()));
    }

    @GetMapping("/schedule")
    public ApiResponse<?> schedule(@AuthenticationPrincipal VogtUserDetails principal) {
        return ApiResponse.ok(studentPortalService.schedule(principal.getUser().getId()));
    }

    @GetMapping("/grades")
    public ApiResponse<?> grades(@AuthenticationPrincipal VogtUserDetails principal,
                                  @RequestParam(required = false) String semester) {
        return ApiResponse.ok(studentPortalService.grades(principal.getUser().getId(), semester));
    }
}
