package cm.vogt.digitalcampus.dto.accounts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateTeacherRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @Email @NotBlank private String email;
    private String phone;
    @NotBlank private String initialPassword;
    private String department;
    private String title;
    private List<String> specialties;
}
