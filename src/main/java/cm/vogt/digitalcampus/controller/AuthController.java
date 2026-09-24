package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.dto.auth.AuthResponse;
import cm.vogt.digitalcampus.dto.auth.LoginRequest;
import cm.vogt.digitalcampus.dto.auth.RegisterCandidateRequest;
import cm.vogt.digitalcampus.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/candidate")
    public ApiResponse<AuthResponse> registerCandidate(@Valid @RequestBody RegisterCandidateRequest request) {
        return ApiResponse.ok("Compte candidat cree.", authService.registerCandidate(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }
}
