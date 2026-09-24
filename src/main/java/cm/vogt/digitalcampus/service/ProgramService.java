package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.BadRequestException;
import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import cm.vogt.digitalcampus.domain.AcademicYear;
import cm.vogt.digitalcampus.domain.Program;
import cm.vogt.digitalcampus.dto.program.ProgramRequest;
import cm.vogt.digitalcampus.dto.program.ProgramResponse;
import cm.vogt.digitalcampus.repository.AcademicYearRepository;
import cm.vogt.digitalcampus.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Toutes les formations publiees sur /formations proviennent de ce service —
 * aucun intitule, aucune duree, aucun tarif n'est ecrit dans le frontend.
 */
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final AcademicYearRepository academicYearRepository;
    private final AuditLogService auditLogService;

    public List<ProgramResponse> listPublished(String category) {
        return listPublished(category, "fr");
    }

    public List<ProgramResponse> listPublished(String category, String lang) {
        List<Program> programs = (category == null || category.isBlank())
                ? programRepository.findByStatusOrderByDisplayOrderAsc(PublicationStatus.PUBLISHED)
                : programRepository.findByStatusAndCategoryOrderByDisplayOrderAsc(PublicationStatus.PUBLISHED, category);
        return programs.stream().map(p -> toResponse(p, lang)).toList();
    }

    /** Utilise par le VOGT ADMIN — toutes les formations, quel que soit leur statut. */
    public List<ProgramResponse> listAllForAdmin() {
        return programRepository.findAll().stream().map(p -> toResponse(p, "fr")).toList();
    }

    public ProgramResponse getPublishedBySlug(String slug) {
        return getPublishedBySlug(slug, "fr");
    }

    public ProgramResponse getPublishedBySlug(String slug, String lang) {
        Program program = programRepository.findBySlugAndStatus(slug, PublicationStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Formation introuvable : " + slug));
        return toResponse(program, lang);
    }

    @Transactional
    public ProgramResponse create(ProgramRequest request) {
        if (programRepository.existsBySlug(request.getSlug())) {
            throw new BadRequestException("Le slug '" + request.getSlug() + "' est deja utilise.");
        }
        Program program = new Program();
        applyRequest(program, request);
        program = programRepository.save(program);
        auditLogService.log("CREATE_PROGRAM", "Program", program.getId().toString(), "Formation creee : " + program.getName());
        return toResponse(program, "fr");
    }

    @Transactional
    public ProgramResponse update(UUID id, ProgramRequest request) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Formation introuvable."));
        applyRequest(program, request);
        program = programRepository.save(program);
        auditLogService.log("UPDATE_PROGRAM", "Program", program.getId().toString(), "Formation modifiee : " + program.getName());
        return toResponse(program, "fr");
    }

    @Transactional
    public void publish(UUID id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Formation introuvable."));
        program.setStatus(PublicationStatus.PUBLISHED);
        programRepository.save(program);
        auditLogService.log("PUBLISH_PROGRAM", "Program", program.getId().toString(), "Formation publiee : " + program.getName());
    }

    @Transactional
    public void unpublish(UUID id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Formation introuvable."));
        program.setStatus(PublicationStatus.DRAFT);
        programRepository.save(program);
        auditLogService.log("UNPUBLISH_PROGRAM", "Program", program.getId().toString(), "Formation depubliee : " + program.getName());
    }

    @Transactional
    public void archive(UUID id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Formation introuvable."));
        program.setStatus(PublicationStatus.ARCHIVED);
        programRepository.save(program);
        auditLogService.log("ARCHIVE_PROGRAM", "Program", program.getId().toString(), "Formation archivee : " + program.getName());
    }

    private void applyRequest(Program program, ProgramRequest request) {
        program.setSlug(request.getSlug());
        program.setName(request.getName());
        program.setCategory(request.getCategory());
        program.setShortDescription(request.getShortDescription());
        program.setFullDescription(request.getFullDescription());
        program.setNameEn(request.getNameEn());
        program.setShortDescriptionEn(request.getShortDescriptionEn());
        program.setFullDescriptionEn(request.getFullDescriptionEn());
        program.setHeroImageUrl(request.getHeroImageUrl());
        program.setDurationLabel(request.getDurationLabel());
        program.setDiplomaLabel(request.getDiplomaLabel());
        program.setLevel(request.getLevel());
        program.setTags(request.getTags());
        program.setTuitionAmountXaf(request.getTuitionAmountXaf());
        program.setDisplayOrder(request.getDisplayOrder());
        if (request.getAcademicYearId() != null) {
            AcademicYear year = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new NotFoundException("Annee academique introuvable."));
            program.setAcademicYear(year);
        }
    }

    private ProgramResponse toResponse(Program p, String lang) {
        boolean en = "en".equalsIgnoreCase(lang);
        return new ProgramResponse(
                p.getId(), p.getSlug(),
                en && notBlank(p.getNameEn()) ? p.getNameEn() : p.getName(),
                p.getCategory(),
                en && notBlank(p.getShortDescriptionEn()) ? p.getShortDescriptionEn() : p.getShortDescription(),
                en && notBlank(p.getFullDescriptionEn()) ? p.getFullDescriptionEn() : p.getFullDescription(),
                p.getHeroImageUrl(), p.getDurationLabel(), p.getDiplomaLabel(),
                p.getLevel(), p.getTags(), p.getTuitionAmountXaf(),
                p.getAcademicYear() != null ? p.getAcademicYear().getLabel() : null,
                p.getStatus() != null ? p.getStatus().name() : null
        );
    }

    private boolean notBlank(String s) { return s != null && !s.isBlank(); }
}
