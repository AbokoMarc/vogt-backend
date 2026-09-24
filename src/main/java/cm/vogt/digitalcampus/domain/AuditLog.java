package cm.vogt.digitalcampus.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Trace toute modification de contenu institutionnel — qui, quand, quoi. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    private String actorEmail;
    private String actorRole;
    private String action;       // ex. "UPDATE_PROGRAM"
    private String entityType;   // ex. "Program"
    private String entityId;
    private String ipAddress;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String detail;
}
