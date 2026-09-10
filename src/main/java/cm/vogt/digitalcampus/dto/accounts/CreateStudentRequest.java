package cm.vogt.digitalcampus.dto.accounts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateStudentRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @Email @NotBlank private String email;
    private String phone;
    /** Optionnel — genere automatiquement si vide. */
    private String initialPassword;
    @NotBlank private String matricule;
    private int yearOfStudy;
    private UUID programId;
}
