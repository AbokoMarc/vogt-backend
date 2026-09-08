package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.BadRequestException;
import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.common.enums.AcademicYearStatus;
import cm.vogt.digitalcampus.common.enums.ApplicationStatus;
import cm.vogt.digitalcampus.domain.AcademicYear;
import cm.vogt.digitalcampus.domain.Candidate;
import cm.vogt.digitalcampus.domain.Program;
import cm.vogt.digitalcampus.domain.Application;
import cm.vogt.digitalcampus.dto.application.ApplicationTrackerResponse;
import cm.vogt.digitalcampus.dto.application.CreateApplicationRequest;
import cm.vogt.digitalcampus.dto.application.UpdateApplicationStatusRequest;
import cm.vogt.digitalcampus.kafka.NotificationProducer;
import cm.vogt.digitalcampus.kafka.event.ApplicationStatusChangedEvent;
import cm.vogt.digitalcampus.repository.AcademicYearRepository;
import cm.vogt.digitalcampus.repository.ApplicationRepository;
import cm.vogt.digitalcampus.repository.CandidateRepository;
import cm.vogt.digitalcampus.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Pipeline de candidature complet : creation -> generation du numero de suivi
 * VHT-{annee}-{sequence} -> changement de statut -> notification (Kafka).
 */
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final ProgramRepository programRepository;
    private final AcademicYearRepository academicYearRepository;
    private final NotificationProducer notificationProducer;
    private final AuditLogService auditLogService;

    @Value("${app.application.tracking-prefix}")
    private String trackingPrefix;

    @Transactional
    public ApplicationTrackerResponse create(UUID candidateId, CreateApplicationRequest request) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new NotFoundException("Candidat introuvable."));

        Program firstChoice = programRepository.findById(request.getFirstChoiceProgramId())
                .orElseThrow(() -> new NotFoundException("Formation (1er choix) introuvable."));

        Program secondChoice = request.getSecondChoiceProgramId() != null
                ? programRepository.findById(request.getSecondChoiceProgramId()).orElse(null)
                : null;

        AcademicYear activeYear = academicYearRepository.findAll().stream()
                .filter(y -> y.getStatus() == AcademicYearStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Aucune annee academique active n'est configuree."));

        Application application = new Application();
        application.setCandidate(candidate);
        application.setFirstChoiceProgram(firstChoice);
        application.setSecondChoiceProgram(secondChoice);
        application.setAcademicYear(activeYear);
        application.setStatus(ApplicationStatus.DRAFT);
        application.setTrackingNumber(generateTrackingNumber(activeYear));
        application = applicationRepository.save(application);

        return toTracker(application);
    }

    @Transactional
    public ApplicationTrackerResponse submit(String trackingNumber) {
        Application application = getByTrackingNumber(trackingNumber);
        changeStatus(application, ApplicationStatus.SUBMITTED);
        return toTracker(application);
    }

    @Transactional
    public ApplicationTrackerResponse updateStatus(String trackingNumber, UpdateApplicationStatusRequest request) {
        Application application = getByTrackingNumber(trackingNumber);
        changeStatus(application, request.getStatus());
        application.setReviewerNote(request.getReviewerNote());
        applicationRepository.save(application);
        return toTracker(application);
    }

    public ApplicationTrackerResponse track(String trackingNumber) {
        return toTracker(getByTrackingNumber(trackingNumber));
    }

    public ApplicationTrackerResponse myLatestApplication(UUID candidateId) {
        Application application = applicationRepository.findFirstByCandidateIdOrderByCreatedAtDesc(candidateId)
                .orElseThrow(() -> new NotFoundException("Aucune candidature trouvee pour ce candidat."));
        return toTracker(application);
    }

    private void changeStatus(Application application, ApplicationStatus newStatus) {
        ApplicationStatus previous = application.getStatus();
        application.setStatus(newStatus);
        applicationRepository.save(application);

        notificationProducer.publishApplicationStatusChanged(new ApplicationStatusChangedEvent(
                application.getTrackingNumber(),
                application.getCandidate().getUser().getEmail(),
                previous.name(),
                newStatus.name()
        ));

        auditLogService.log("CHANGE_APPLICATION_STATUS", "Application", application.getTrackingNumber(),
                previous.name() + " -> " + newStatus.name());
    }

    private Application getByTrackingNumber(String trackingNumber) {
        return applicationRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new NotFoundException("Candidature introuvable : " + trackingNumber));
    }

    private String generateTrackingNumber(AcademicYear year) {
        long sequence = applicationRepository.countByAcademicYearId(year.getId()) + 1;
        String yearDigits = year.getLabel() != null ? year.getLabel().substring(0, 4) : "0000";
        return String.format("%s-%s-%05d", trackingPrefix, yearDigits, sequence);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<cm.vogt.digitalcampus.dto.admin.ApplicationAdminResponse> listAllForAdmin() {
        return applicationRepository.findAll().stream()
                .map(this::toAdminResponse)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    private cm.vogt.digitalcampus.dto.admin.ApplicationAdminResponse toAdminResponse(Application a) {
        var candidateUser = a.getCandidate() != null ? a.getCandidate().getUser() : null;
        return new cm.vogt.digitalcampus.dto.admin.ApplicationAdminResponse(
                a.getTrackingNumber(),
                candidateUser != null ? (candidateUser.getFirstName() + " " + candidateUser.getLastName()) : null,
                candidateUser != null ? candidateUser.getEmail() : null,
                candidateUser != null ? candidateUser.getPhone() : null,
                a.getFirstChoiceProgram() != null ? a.getFirstChoiceProgram().getName() : null,
                a.getSecondChoiceProgram() != null ? a.getSecondChoiceProgram().getName() : null,
                a.getAcademicYear() != null ? a.getAcademicYear().getLabel() : null,
                a.getStatus().name(),
                a.getReviewerNote(),
                a.getCreatedAt()
        );
    }

    private ApplicationTrackerResponse toTracker(Application a) {
        return new ApplicationTrackerResponse(
                a.getTrackingNumber(),
                a.getStatus().name(),
                a.getFirstChoiceProgram() != null ? a.getFirstChoiceProgram().getName() : null,
                a.getAcademicYear() != null ? a.getAcademicYear().getLabel() : null
        );
    }
}
