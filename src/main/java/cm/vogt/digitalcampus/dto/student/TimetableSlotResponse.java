package cm.vogt.digitalcampus.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimetableSlotResponse {
    private String courseName;
    private String teacherName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String room;
}
