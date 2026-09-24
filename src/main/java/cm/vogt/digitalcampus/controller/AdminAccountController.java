package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.dto.accounts.*;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import cm.vogt.digitalcampus.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AccountService accountService;

    /* ---- Enseignants ---- */

    @PostMapping("/teachers")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ACADEMIC_ADMIN')")
    public ApiResponse<Void> createTeacher(@AuthenticationPrincipal VogtUserDetails principal,
                                            @Valid @RequestBody CreateTeacherRequest request) {
        accountService.createTeacher(request, principal.getUser().getEmail());
        return ApiResponse.ok("Compte enseignant créé. Un email lui a été envoyé.", null);
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ACADEMIC_ADMIN')")
    public ApiResponse<?> listTeachers() {
        return ApiResponse.ok(accountService.listTeachers());
    }

    /* ---- Etudiants ---- */

    @PostMapping("/students")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ACADEMIC_ADMIN','ADMISSIONS_OFFICER')")
    public ApiResponse<Void> createStudent(@AuthenticationPrincipal VogtUserDetails principal,
                                            @Valid @RequestBody CreateStudentRequest request) {
        accountService.createStudent(request, principal.getUser().getEmail());
        return ApiResponse.ok("Compte étudiant créé. Un email lui a été envoyé.", null);
    }

    /* ---- Activation / desactivation (jamais de suppression definitive) ---- */

    @PostMapping("/{userId}/deactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ACADEMIC_ADMIN','ADMISSIONS_OFFICER')")
    public ApiResponse<Void> deactivate(@PathVariable UUID userId) {
        accountService.setAccountActive(userId, false);
        return ApiResponse.ok("Compte désactivé (réactivable à tout moment).", null);
    }

    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ACADEMIC_ADMIN','ADMISSIONS_OFFICER')")
    public ApiResponse<Void> activate(@PathVariable UUID userId) {
        accountService.setAccountActive(userId, true);
        return ApiResponse.ok("Compte réactivé.", null);
    }

    /* ---- Comptes admin (hierarchie discrete SUPER_ADMIN / ADMIN / sous-admins) ---- */

    @PostMapping("/admins")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<Void> createAdminAccount(@AuthenticationPrincipal VogtUserDetails principal,
                                                 @Valid @RequestBody CreateAdminAccountRequest request) {
        accountService.createAdminAccount(request, principal.getUser().getRole(), principal.getUser().getEmail());
        return ApiResponse.ok("Compte administrateur créé.", null);
    }

    @GetMapping("/admins")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ApiResponse<List<AccountSummaryResponse>> listAdmins(@AuthenticationPrincipal VogtUserDetails principal) {
        return ApiResponse.ok(accountService.listAdminAccounts(principal.getUser().getRole()));
    }
}
