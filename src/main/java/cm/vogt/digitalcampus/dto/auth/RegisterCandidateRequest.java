package cm.vogt.digitalcampus.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterCandidateRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @Email @NotBlank private String email;
    @NotBlank private String phone;
    @NotBlank private String password;
    private LocalDate dateOfBirth;
    private String bacSeries;
    private String highestDiploma;
}
