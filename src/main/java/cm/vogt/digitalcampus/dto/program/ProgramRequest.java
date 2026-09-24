package cm.vogt.digitalcampus.dto.program;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/** Utilise par le CMS (VOGT ADMIN) pour creer/modifier une formation — zero hardcode cote front. */
@Data
public class ProgramRequest {
    @NotBlank private String slug;
    @NotBlank private String name;
    private String category;
    private String shortDescription;
    private String fullDescription;
    private String nameEn;
    private String shortDescriptionEn;
    private String fullDescriptionEn;
    private String heroImageUrl;
    private String durationLabel;
    private String diplomaLabel;
    private String level;
    private List<String> tags;
    private UUID academicYearId;
    private Long tuitionAmountXaf;
    private int displayOrder;
}
