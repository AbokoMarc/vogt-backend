package cm.vogt.digitalcampus.dto.application;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateApplicationRequest {
    @NotNull private UUID firstChoiceProgramId;
    private UUID secondChoiceProgramId;
}
