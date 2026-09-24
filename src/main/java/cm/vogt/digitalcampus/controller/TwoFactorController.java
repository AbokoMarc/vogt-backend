package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.common.BadRequestException;
import cm.vogt.digitalcampus.domain.User;
import cm.vogt.digitalcampus.repository.UserRepository;
import cm.vogt.digitalcampus.security.TotpService;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Activation du 2FA (TOTP) pour les comptes administrateurs. Flux :
 * 1. POST /setup -> genere un secret + URI otpauth (a afficher en QR code cote front)
 * 2. POST /enable {code} -> verifie le premier code saisi, active le 2FA
 * 3. POST /disable -> desactive (protege par mot de passe deja verifie via le token)
 */
@RestController
@RequestMapping("/api/v1/admin/2fa")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ADMISSIONS_OFFICER','ACADEMIC_ADMIN','COMMUNICATION_ADMIN')")
public class TwoFactorController {

    private final TotpService totpService;
    private final UserRepository userRepository;

    @PostMapping("/setup")
    public ApiResponse<Map<String, String>> setup(@AuthenticationPrincipal VogtUserDetails principal) {
        User user = userRepository.findById(principal.getUser().getId()).orElseThrow();
        String secret = totpService.generateSecret();
        user.setTwoFactorSecret(secret);
        user.setTwoFactorEnabled(false); // pas actif tant que /enable n'a pas verifie un code
        userRepository.save(user);

        String otpAuthUri = totpService.buildOtpAuthUri(secret, user.getEmail());
        return ApiResponse.ok(Map.of("secret", secret, "otpAuthUri", otpAuthUri));
    }

    @PostMapping("/enable")
    public ApiResponse<Void> enable(@AuthenticationPrincipal VogtUserDetails principal, @RequestParam String code) {
        User user = userRepository.findById(principal.getUser().getId()).orElseThrow();
        if (user.getTwoFactorSecret() == null) {
            throw new BadRequestException("Lancez d'abord /setup pour generer un secret.");
        }
        if (!totpService.verifyCode(user.getTwoFactorSecret(), code)) {
            throw new BadRequestException("Code invalide.");
        }
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
        return ApiResponse.ok("2FA active avec succes.", null);
    }

    @PostMapping("/disable")
    public ApiResponse<Void> disable(@AuthenticationPrincipal VogtUserDetails principal) {
        User user = userRepository.findById(principal.getUser().getId()).orElseThrow();
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
        return ApiResponse.ok("2FA desactive.", null);
    }
}
