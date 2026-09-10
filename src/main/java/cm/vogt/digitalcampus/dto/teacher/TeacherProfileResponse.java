package cm.vogt.digitalcampus.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeacherProfileResponse {
    private String firstName;
    private String lastName;
    private String department;
    private String title;
}
