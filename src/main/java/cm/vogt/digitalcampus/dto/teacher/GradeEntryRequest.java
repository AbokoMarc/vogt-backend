package cm.vogt.digitalcampus.dto.teacher;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GradeEntryRequest {
    @NotNull private UUID studentId;
    @NotNull private UUID courseId;
    @NotBlank private String semester;
    @DecimalMin("0.0") @DecimalMax("20.0") private double score;
}
