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
        return academicYearRepository.findAll().stream().filter(y -> !y.isDeleted()).toList();
    }

    /** Corbeille — annees supprimees il y a moins de 30 jours, restaurables. */
    public List<AcademicYear> listTrash() {
        java.time.Instant cutoff = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS);
        return academicYearRepository.findAll().stream()
                .filter(y -> y.isDeleted() && y.getDeletedAt() != null && y.getDeletedAt().isAfter(cutoff))
                .toList();
    }

    @Transactional
    public void softDelete(UUID id) {
        AcademicYear year = academicYearRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annee academique introuvable."));
        if (year.getStatus() == AcademicYearStatus.ACTIVE) {
            throw new cm.vogt.digitalcampus.common.BadRequestException(
                    "Impossible de supprimer l'annee active. Activez une autre annee d'abord.");
        }
        year.setDeleted(true);
        year.setDeletedAt(java.time.Instant.now());
        academicYearRepository.save(year);
        auditLogService.log("DELETE_ACADEMIC_YEAR", "AcademicYear", year.getId().toString(),
                "Annee supprimee (restaurable 30 jours) : " + year.getLabel());
    }

    @Transactional
    public void restore(UUID id) {
        AcademicYear year = academicYearRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annee academique introuvable."));
        java.time.Instant cutoff = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS);
        if (!year.isDeleted() || year.getDeletedAt() == null || year.getDeletedAt().isBefore(cutoff)) {
            throw new cm.vogt.digitalcampus.common.BadRequestException(
                    "Cette annee ne peut plus etre restauree (delai de 30 jours depasse ou deja active).");
        }
        year.setDeleted(false);
        year.setDeletedAt(null);
        academicYearRepository.save(year);
        auditLogService.log("RESTORE_ACADEMIC_YEAR", "AcademicYear", year.getId().toString(), "Annee restauree : " + year.getLabel());
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
