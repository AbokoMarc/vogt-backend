package cm.vogt.digitalcampus.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountSummaryResponse {
    private java.util.UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean active;
    private String createdByEmail;
    private java.time.Instant createdAt;
}
