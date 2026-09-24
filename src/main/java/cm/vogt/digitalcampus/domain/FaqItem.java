package cm.vogt.digitalcampus.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "faq_items")
public class FaqItem extends BaseEntity {

    private String question;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String answer;

    private String questionEn;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String answerEn;

    private String category; // ADMISSIONS, FORMATIONS, VIE_ETUDIANTE, GENERAL
    private int displayOrder = 0;
}
