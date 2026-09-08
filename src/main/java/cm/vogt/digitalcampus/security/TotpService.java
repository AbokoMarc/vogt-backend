package cm.vogt.digitalcampus.security;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * TOTP (RFC 6238) implemente en Java pur — aucune dependance externe.
 * Compatible Google Authenticator / Microsoft Authenticator / Authy.
 * Utilise pour le 2FA des comptes administrateurs (section 40 du cahier des charges).
 */
@Service
public class TotpService {

    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;
    private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    /** Genere un secret aleatoire encode en Base32, a stocker cote serveur (jamais en clair au client apres setup). */
    public String generateSecret() {
        byte[] randomBytes = new byte[20];
        new SecureRandom().nextBytes(randomBytes);
        return base32Encode(randomBytes);
    }

    /** URI otpauth:// a encoder en QR code par le client (ex. via une librairie QR cote frontend). */
    public String buildOtpAuthUri(String secret, String accountEmail) {
        return String.format(
                "otpauth://totp/VogtHighTech:%s?secret=%s&issuer=VogtHighTech&digits=%d&period=%d",
                accountEmail, secret, CODE_DIGITS, TIME_STEP_SECONDS
        );
    }

    public boolean verifyCode(String secret, String code) {
        if (code == null || !code.matches("\\d{6}")) return false;
        long currentWindow = System.currentTimeMillis() / 1000 / TIME_STEP_SECONDS;
        // Tolerance d'une fenetre avant/apres pour absorber le decalage d'horloge du mobile.
        for (long window = currentWindow - 1; window <= currentWindow + 1; window++) {
            if (generateCode(secret, window).equals(code)) return true;
        }
        return false;
    }

    private String generateCode(String base32Secret, long timeWindow) {
        try {
            byte[] key = base32Decode(base32Secret);
            byte[] data = ByteBuffer.allocate(8).putLong(timeWindow).array();

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, CODE_DIGITS);
            return String.format("%0" + CODE_DIGITS + "d", otp);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de generation TOTP", e);
        }
    }

    private String base32Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int bits = 0, value = 0;
        for (byte b : data) {
            value = (value << 8) | (b & 0xFF);
            bits += 8;
            while (bits >= 5) {
                sb.append(BASE32_ALPHABET.charAt((value >> (bits - 5)) & 31));
                bits -= 5;
            }
        }
        if (bits > 0) sb.append(BASE32_ALPHABET.charAt((value << (5 - bits)) & 31));
        return sb.toString();
    }

    private byte[] base32Decode(String base32) {
        base32 = base32.trim().toUpperCase().replace("=", "");
        int bits = 0, value = 0, index = 0;
        byte[] output = new byte[base32.length() * 5 / 8];
        for (char c : base32.toCharArray()) {
            value = (value << 5) | BASE32_ALPHABET.indexOf(c);
            bits += 5;
            if (bits >= 8) {
                output[index++] = (byte) ((value >> (bits - 8)) & 0xFF);
                bits -= 8;
            }
        }
        return output;
    }
}
