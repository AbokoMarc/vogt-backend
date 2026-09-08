package cm.vogt.digitalcampus.dto.application;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {
    @NotNull private cm.vogt.digitalcampus.common.enums.ApplicationStatus status;
    private String reviewerNote;
}
