package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.security.VogtUserDetails;
import cm.vogt.digitalcampus.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students/me/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentPaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ApiResponse<?> myPayments(@AuthenticationPrincipal VogtUserDetails principal) {
        return ApiResponse.ok(paymentService.myPayments(principal.getUser().getId()));
    }
}
