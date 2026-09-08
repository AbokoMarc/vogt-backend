package cm.vogt.digitalcampus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;

/**
 * Envoi de messages WhatsApp via l'API Twilio (WhatsApp Business API).
 * Desactive tant que TWILIO_ACCOUNT_SID / TWILIO_AUTH_TOKEN / TWILIO_WHATSAPP_FROM
 * ne sont pas renseignes — les envois sont alors seulement journalises.
 *
 * Alternative possible sans changer l'appelant : remplacer l'implementation
 * de send() par un appel a l'API Cloud Meta si vous preferez ce fournisseur.
 */
@Slf4j
@Service
public class WhatsAppService {

    @Value("${app.whatsapp.enabled:false}")
    private boolean enabled;

    @Value("${app.whatsapp.account-sid:}")
    private String accountSid;

    @Value("${app.whatsapp.auth-token:}")
    private String authToken;

    @Value("${app.whatsapp.from-number:}")
    private String fromNumber; // format Twilio : "whatsapp:+14155238886"

    public void send(String toPhoneNumber, String message) {
        if (!enabled || accountSid.isBlank() || authToken.isBlank()) {
            log.info("[WHATSAPP desactive] A: {} | Message: {}", toPhoneNumber, message);
            return;
        }

        try {
            String credentials = Base64.getEncoder().encodeToString((accountSid + ":" + authToken).getBytes());

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("To", "whatsapp:" + toPhoneNumber);
            form.add("From", fromNumber);
            form.add("Body", message);

            RestClient client = RestClient.create();
            var response = client.post()
                    .uri("https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();

            if (response.getStatusCode() != HttpStatus.CREATED) {
                log.warn("Reponse Twilio inattendue : {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Echec d'envoi WhatsApp a {} : {}", toPhoneNumber, e.getMessage());
        }
    }
}
