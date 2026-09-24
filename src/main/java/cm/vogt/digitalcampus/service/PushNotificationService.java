package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.domain.PushSubscription;
import cm.vogt.digitalcampus.domain.User;
import cm.vogt.digitalcampus.repository.PushSubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.List;

/**
 * Notifications push navigateur/telephone (protocole Web Push + VAPID).
 * S'affichent comme de vraies notifications systeme, meme application/onglet
 * ferme — c'est le service-worker cote client qui les reçoit et les affiche.
 *
 * Pour l'activer en production :
 * 1. Generer une paire de cles VAPID (ex. via `npx web-push generate-vapid-keys`)
 * 2. Renseigner VAPID_PUBLIC_KEY, VAPID_PRIVATE_KEY, VAPID_SUBJECT dans les
 *    variables d'environnement.
 * Sans ces cles, les envois sont simplement journalises (aucune erreur).
 */
@Slf4j
@Service
public class PushNotificationService {

    private final PushSubscriptionRepository pushSubscriptionRepository;

    @Value("${app.push.public-key:}")
    private String publicKey;

    @Value("${app.push.private-key:}")
    private String privateKey;

    @Value("${app.push.subject:mailto:admin@vogthightech.cm}")
    private String subject;

    public PushNotificationService(PushSubscriptionRepository pushSubscriptionRepository) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public boolean isConfigured() {
        return publicKey != null && !publicKey.isBlank() && privateKey != null && !privateKey.isBlank();
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void sendToUser(User user, String title, String body) {
        if (!isConfigured()) {
            log.info("[PUSH desactive] Pour {} : {} — {}", user.getEmail(), title, body);
            return;
        }

        List<PushSubscription> subs = pushSubscriptionRepository.findByUserId(user.getId());
        if (subs.isEmpty()) return;

        try {
            PushService pushService = new PushService(publicKey, privateKey, subject);
            String payload = "{\"title\":\"" + escape(title) + "\",\"body\":\"" + escape(body) + "\"}";

            for (PushSubscription sub : subs) {
                try {
                    Notification notification = new Notification(
                            sub.getEndpoint(), sub.getP256dh(), sub.getAuth(), payload
                    );
                    pushService.send(notification);
                } catch (Exception e) {
                    log.warn("Echec envoi push a l'abonnement {} : {}", sub.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Erreur service Push : {}", e.getMessage());
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
