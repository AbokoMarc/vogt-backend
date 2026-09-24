package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.domain.AuditLog;
import cm.vogt.digitalcampus.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Trace chaque action d'administration (qui, quoi, quand). Appele depuis les
 * services CMS (Program, News, Event, Application, AcademicYear...) — jamais
 * depuis les controleurs, pour garantir qu'aucune action ne passe au travers.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String entityType, String entityId, String detail) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actorEmail = (auth != null) ? auth.getName() : "system";
        String actorRole = (auth != null && !auth.getAuthorities().isEmpty())
                ? auth.getAuthorities().iterator().next().getAuthority()
                : "SYSTEM";

        AuditLog log = new AuditLog();
        log.setActorEmail(actorEmail);
        log.setActorRole(actorRole);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetail(detail);
        auditLogRepository.save(log);
    }
}
