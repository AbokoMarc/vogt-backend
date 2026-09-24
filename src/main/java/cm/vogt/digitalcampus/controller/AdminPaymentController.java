package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.Payment;
import cm.vogt.digitalcampus.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** Gestion des paiements/scolarite — reserve aux administrateurs et bureau des admissions. */
@RestController
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ADMISSIONS_OFFICER')")
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ApiResponse<java.util.List<cm.vogt.digitalcampus.domain.Payment>> listAll() {
        return ApiResponse.ok(paymentService.listAllForAdmin());
    }

    @PostMapping
    public ApiResponse<Payment> createInvoice(@RequestParam UUID studentId,
                                               @RequestParam Long amountXaf,
                                               @RequestParam String label) {
        return ApiResponse.ok("Facture creee.", paymentService.createInvoice(studentId, amountXaf, label));
    }

    @PostMapping("/{id}/mark-paid")
    public ApiResponse<Payment> markPaid(@PathVariable UUID id, @RequestParam(required = false) String receiptUrl) {
        return ApiResponse.ok("Paiement enregistre.", paymentService.markPaid(id, receiptUrl));
    }
}
