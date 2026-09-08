package cm.vogt.digitalcampus.dto.content;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewsRequest {
    @NotBlank private String slug;
    @NotBlank private String title;
    private String category;
    private String coverImageUrl;
    private String excerpt;
    private String content;
    private String titleEn;
    private String excerptEn;
    private String contentEn;
    private String author;
    private String seoTitle;
    private String metaDescription;
}
