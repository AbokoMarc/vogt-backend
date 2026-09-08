package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.BadRequestException;
import cm.vogt.digitalcampus.common.enums.Role;
import cm.vogt.digitalcampus.domain.Candidate;
import cm.vogt.digitalcampus.domain.User;
import cm.vogt.digitalcampus.dto.auth.AuthResponse;
import cm.vogt.digitalcampus.dto.auth.LoginRequest;
import cm.vogt.digitalcampus.dto.auth.RegisterCandidateRequest;
import cm.vogt.digitalcampus.repository.CandidateRepository;
import cm.vogt.digitalcampus.repository.UserRepository;
import cm.vogt.digitalcampus.security.JwtService;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final cm.vogt.digitalcampus.security.TotpService totpService;

    @Transactional
    public AuthResponse registerCandidate(RegisterCandidateRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BadRequestException("Un compte existe deja avec cet email.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(Role.CANDIDATE);
        user = userRepository.save(user);

        Candidate candidate = new Candidate();
        candidate.setUser(user);
        candidate.setDateOfBirth(request.getDateOfBirth());
        candidate.setBacSeries(request.getBacSeries());
        candidate.setHighestDiploma(request.getHighestDiploma());
        candidateRepository.save(candidate);

        VogtUserDetails userDetails = new VogtUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails, Map.of("role", user.getRole().name()));
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        return new AuthResponse(accessToken, refreshToken, user.getRole().name(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Identifiants invalides."));

        if (user.isTwoFactorEnabled()) {
            if (request.getCode() == null || request.getCode().isBlank()) {
                throw new BadRequestException("Ce compte est protege par le 2FA : code a 6 chiffres requis.");
            }
            if (!totpService.verifyCode(user.getTwoFactorSecret(), request.getCode())) {
                throw new BadRequestException("Code 2FA invalide.");
            }
        }

        VogtUserDetails userDetails = new VogtUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails, Map.of("role", user.getRole().name()));
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        return new AuthResponse(accessToken, refreshToken, user.getRole().name(), user.getEmail());
    }
}
