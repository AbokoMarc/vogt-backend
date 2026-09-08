package cm.vogt.digitalcampus.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ApplicationAdminResponse {
    private String trackingNumber;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String firstChoiceProgram;
    private String secondChoiceProgram;
    private String academicYear;
    private String status;
    private String reviewerNote;
    private Instant createdAt;
}
