package cm.vogt.digitalcampus.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank private String email;
    @NotBlank private String password;
    /** Requis uniquement si le compte a le 2FA active (voir /admin/2fa/**). */
    private String code;
}
