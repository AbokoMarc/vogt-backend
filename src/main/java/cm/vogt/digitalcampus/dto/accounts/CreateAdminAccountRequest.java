package cm.vogt.digitalcampus.dto.accounts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAdminAccountRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @Email @NotBlank private String email;
    private String phone;
    /** Optionnel — genere automatiquement si vide. */
    private String initialPassword;
    /** ADMIN (uniquement par SUPER_ADMIN), ou ADMISSIONS_OFFICER / ACADEMIC_ADMIN / COMMUNICATION_ADMIN. */
    @NotBlank private String role;
}
