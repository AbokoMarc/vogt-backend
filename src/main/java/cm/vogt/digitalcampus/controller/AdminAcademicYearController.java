package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.AcademicYear;
import cm.vogt.digitalcampus.service.AcademicYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/academic-years")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ACADEMIC_ADMIN')")
public class AdminAcademicYearController {

    private final AcademicYearService academicYearService;

    @GetMapping
    public ApiResponse<List<AcademicYear>> list() {
        return ApiResponse.ok(academicYearService.listAll());
    }

    @PostMapping
    public ApiResponse<AcademicYear> create(@RequestBody AcademicYear year) {
        return ApiResponse.ok("Annee academique creee (UPCOMING).", academicYearService.create(year));
    }

    @PostMapping("/{id}/activate")
    public ApiResponse<AcademicYear> activate(@PathVariable UUID id) {
        return ApiResponse.ok("Annee academique activee ; l'annee precedente est archivee.",
                academicYearService.activate(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        academicYearService.softDelete(id);
        return ApiResponse.ok("Annee supprimee — restaurable pendant 30 jours.", null);
    }

    @GetMapping("/trash")
    public ApiResponse<List<AcademicYear>> trash() {
        return ApiResponse.ok(academicYearService.listTrash());
    }

    @PostMapping("/{id}/restore")
    public ApiResponse<Void> restore(@PathVariable UUID id) {
        academicYearService.restore(id);
        return ApiResponse.ok("Annee academique restauree.", null);
    }
}
