package cm.vogt.digitalcampus.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GradeResponse {
    private String courseName;
    private String semester;
    private double score;
}
