package cm.vogt.digitalcampus.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentProfileResponse {
    private String matricule;
    private String firstName;
    private String lastName;
    private String programName;
    private String specializationName;
    private int yearOfStudy;
}
