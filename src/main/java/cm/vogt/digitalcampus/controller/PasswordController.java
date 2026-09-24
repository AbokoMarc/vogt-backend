package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.dto.password.ChangePasswordRequest;
import cm.vogt.digitalcampus.dto.password.ForgotPasswordRequest;
import cm.vogt.digitalcampus.dto.password.ResetPasswordRequest;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import cm.vogt.digitalcampus.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Mot de passe oublie / reinitialisation / changement — pour tous les roles. */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.requestReset(request.getEmail());
        return ApiResponse.ok("Si un compte existe avec cet email, un lien de réinitialisation a été envoyé.", null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request.getEmail(), request.getToken(), request.getNewPassword());
        return ApiResponse.ok("Mot de passe réinitialisé avec succès.", null);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal VogtUserDetails principal,
                                             @Valid @RequestBody ChangePasswordRequest request) {
        passwordService.changePassword(principal.getUser().getId(), request.getCurrentPassword(), request.getNewPassword());
        return ApiResponse.ok("Mot de passe mis à jour.", null);
    }
}
