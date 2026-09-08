package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.PushSubscription;
import cm.vogt.digitalcampus.domain.User;
import cm.vogt.digitalcampus.repository.PushSubscriptionRepository;
import cm.vogt.digitalcampus.repository.UserRepository;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import cm.vogt.digitalcampus.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Abonnement aux notifications push — reserve aux comptes authentifies
 * (etudiant, enseignant, admin). Jamais propose aux visiteurs publics ou
 * candidats (cote frontend, le bouton d'activation n'apparait que dans
 * ces trois espaces).
 */
@RestController
@RequestMapping("/api/v1/notifications/push")
@RequiredArgsConstructor
public class PushSubscriptionController {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;

    @GetMapping("/vapid-public-key")
    public ApiResponse<Map<String, String>> vapidPublicKey() {
        return ApiResponse.ok(Map.of("publicKey", pushNotificationService.getPublicKey()));
    }

    @PostMapping("/subscribe")
    public ApiResponse<Void> subscribe(@AuthenticationPrincipal VogtUserDetails principal,
                                        @RequestBody Map<String, Object> subscription) {
        User user = userRepository.findById(principal.getUser().getId()).orElseThrow();
        @SuppressWarnings("unchecked")
        Map<String, String> keys = (Map<String, String>) subscription.get("keys");

        PushSubscription sub = new PushSubscription();
        sub.setUser(user);
        sub.setEndpoint((String) subscription.get("endpoint"));
        sub.setP256dh(keys.get("p256dh"));
        sub.setAuth(keys.get("auth"));
        pushSubscriptionRepository.save(sub);

        return ApiResponse.ok("Notifications push activées.", null);
    }

    @PostMapping("/unsubscribe")
    public ApiResponse<Void> unsubscribe(@RequestParam String endpoint) {
        pushSubscriptionRepository.deleteByEndpoint(endpoint);
        return ApiResponse.ok("Notifications push désactivées.", null);
    }
}
