package cm.vogt.digitalcampus.dto.program;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramResponse {
    private UUID id;
    private String slug;
    private String name;
    private String category;
    private String shortDescription;
    private String fullDescription;
    private String heroImageUrl;
    private String durationLabel;
    private String diplomaLabel;
    private String level;
    private List<String> tags;
    private Long tuitionAmountXaf;
    private String academicYearLabel;
    private String status;
}
