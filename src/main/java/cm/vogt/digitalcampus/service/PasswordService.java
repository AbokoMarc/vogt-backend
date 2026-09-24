package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.BadRequestException;
import cm.vogt.digitalcampus.domain.User;
import cm.vogt.digitalcampus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

/**
 * Mot de passe oublie / reinitialisation / changement — pour tous les roles
 * (candidat, etudiant, enseignant, admin). Le token de reset n'est jamais
 * stocke en clair : seul son hash est conserve, avec expiration (1h).
 */
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void requestReset(String email) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            String rawToken = generateToken();
            user.setPasswordResetTokenHash(hash(rawToken));
            user.setPasswordResetExpiry(Instant.now().plus(1, ChronoUnit.HOURS));
            userRepository.save(user);

            String resetLink = "https://votre-site/reset-password.html?token=" + rawToken + "&email=" + user.getEmail();
            emailService.send(
                    user.getEmail(),
                    "Réinitialisation de votre mot de passe — VOGT HIGH TECH",
                    "Bonjour " + user.getFirstName() + ",\n\n" +
                            "Une demande de réinitialisation de mot de passe a été faite pour ce compte.\n" +
                            "Lien de réinitialisation (valable 1 heure) : " + resetLink + "\n\n" +
                            "Si vous n'êtes pas à l'origine de cette demande, ignorez ce message.\n\n" +
                            "VOGT HIGH TECH — Une école de l'INUCASTY"
            );
        });
        // Reponse volontairement identique que l'email existe ou non (evite l'enumeration de comptes).
    }

    @Transactional
    public void resetPassword(String email, String rawToken, String newPassword) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Lien de réinitialisation invalide."));

        if (user.getPasswordResetTokenHash() == null || user.getPasswordResetExpiry() == null
                || user.getPasswordResetExpiry().isBefore(Instant.now())
                || !BCrypt.checkpw(rawToken, user.getPasswordResetTokenHash())) {
            throw new BadRequestException("Lien de réinitialisation invalide ou expiré.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetTokenHash(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Utilisateur introuvable."));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadRequestException("Mot de passe actuel incorrect.");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        return BCrypt.hashpw(rawToken, BCrypt.gensalt());
    }
}
