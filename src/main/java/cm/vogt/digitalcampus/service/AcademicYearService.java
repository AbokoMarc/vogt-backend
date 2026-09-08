package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.common.enums.AcademicYearStatus;
import cm.vogt.digitalcampus.domain.AcademicYear;
import cm.vogt.digitalcampus.repository.AcademicYearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Bascule d'annee academique : activer une nouvelle annee archive automatiquement
 * l'ancienne. C'est le mecanisme qui evite de "repartir sur le code" chaque annee.
 */
@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final AuditLogService auditLogService;

    public List<AcademicYear> listAll() {
        return academicYearRepository.findAll();
    }

    @Transactional
    public AcademicYear activate(UUID id) {
        AcademicYear toActivate = academicYearRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annee academique introuvable."));

        academicYearRepository.findAll().stream()
                .filter(y -> y.getStatus() == AcademicYearStatus.ACTIVE)
                .forEach(y -> {
                    y.setStatus(AcademicYearStatus.ARCHIVED);
                    academicYearRepository.save(y);
                });

        toActivate.setStatus(AcademicYearStatus.ACTIVE);
        toActivate = academicYearRepository.save(toActivate);
        auditLogService.log("ACTIVATE_ACADEMIC_YEAR", "AcademicYear", toActivate.getId().toString(), "Annee activee : " + toActivate.getLabel());
        return toActivate;
    }

    @Transactional
    public AcademicYear create(AcademicYear year) {
        year.setStatus(AcademicYearStatus.UPCOMING);
        year = academicYearRepository.save(year);
        auditLogService.log("CREATE_ACADEMIC_YEAR", "AcademicYear", year.getId().toString(), "Annee creee : " + year.getLabel());
        return year;
    }
}
