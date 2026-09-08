package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.Candidate;
import cm.vogt.digitalcampus.dto.application.ApplicationTrackerResponse;
import cm.vogt.digitalcampus.dto.application.CreateApplicationRequest;
import cm.vogt.digitalcampus.repository.CandidateRepository;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import cm.vogt.digitalcampus.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Portail candidat — creation, soumission, et suivi de sa propre candidature. */
@RestController
@RequestMapping("/api/v1/candidates/applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateApplicationController {

    private final ApplicationService applicationService;
    private final CandidateRepository candidateRepository;

    @PostMapping
    public ApiResponse<ApplicationTrackerResponse> create(
            @AuthenticationPrincipal VogtUserDetails principal,
            @Valid @RequestBody CreateApplicationRequest request) {
        Candidate candidate = candidateRepository.findByUserId(principal.getUser().getId())
                .orElseThrow();
        return ApiResponse.ok("Candidature creee en brouillon.",
                applicationService.create(candidate.getId(), request));
    }

    @PostMapping("/{trackingNumber}/submit")
    public ApiResponse<ApplicationTrackerResponse> submit(@PathVariable String trackingNumber) {
        return ApiResponse.ok("Candidature soumise.", applicationService.submit(trackingNumber));
    }

    @GetMapping("/{trackingNumber}")
    public ApiResponse<ApplicationTrackerResponse> track(@PathVariable String trackingNumber) {
        return ApiResponse.ok(applicationService.track(trackingNumber));
    }

    /** Utilise par le portail candidat pour retrouver son dossier sans connaitre le numero de suivi. */
    @GetMapping("/me")
    public ApiResponse<ApplicationTrackerResponse> myLatest(@AuthenticationPrincipal VogtUserDetails principal) {
        Candidate candidate = candidateRepository.findByUserId(principal.getUser().getId())
                .orElseThrow();
        return ApiResponse.ok(applicationService.myLatestApplication(candidate.getId()));
    }
}
