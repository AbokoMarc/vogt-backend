package cm.vogt.digitalcampus.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class StudentBriefResponse {
    private UUID id;
    private String matricule;
    private String firstName;
    private String lastName;
    private String programName;
}
