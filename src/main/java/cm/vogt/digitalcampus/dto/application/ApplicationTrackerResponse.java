package cm.vogt.digitalcampus.dto.application;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationTrackerResponse {
    private String trackingNumber;
    private String status;
    private String firstChoiceProgram;
    private String academicYear;
}
