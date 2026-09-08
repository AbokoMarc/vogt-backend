package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.domain.Application;
import cm.vogt.digitalcampus.domain.ApplicationDocument;
import cm.vogt.digitalcampus.repository.ApplicationDocumentRepository;
import cm.vogt.digitalcampus.repository.ApplicationRepository;
import cm.vogt.digitalcampus.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/** Upload des pieces justificatives d'une candidature (acte de naissance, diplome, CNI, photo...). */
@RestController
@RequestMapping("/api/v1/candidates/applications/{trackingNumber}/documents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATE')")
public class DocumentUploadController {

    private final FileStorageService fileStorageService;
    private final ApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository applicationDocumentRepository;

    @PostMapping
    public ApiResponse<ApplicationDocument> upload(@PathVariable String trackingNumber,
                                                     @RequestParam String documentType,
                                                     @RequestParam MultipartFile file) throws IOException {
        Application application = applicationRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new NotFoundException("Candidature introuvable."));

        String url = fileStorageService.store(file, "applications/" + trackingNumber);

        ApplicationDocument document = new ApplicationDocument();
        document.setApplication(application);
        document.setDocumentType(documentType);
        document.setOriginalFileName(file.getOriginalFilename());
        document.setFileUrl(url);
        applicationDocumentRepository.save(document);

        return ApiResponse.ok("Document televerse.", document);
    }
}
