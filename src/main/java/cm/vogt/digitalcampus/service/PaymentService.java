package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.common.enums.PaymentStatus;
import cm.vogt.digitalcampus.domain.Payment;
import cm.vogt.digitalcampus.domain.Student;
import cm.vogt.digitalcampus.repository.PaymentRepository;
import cm.vogt.digitalcampus.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Scolarite — les montants proviennent toujours de Program.tuitionAmountXaf
 * de l'annee active ; cette classe ne fait que suivre les echeances/paiements,
 * jamais de montant fige en dur.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    public List<Payment> myPayments(UUID userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil etudiant introuvable."));
        return paymentRepository.findAll().stream()
                .filter(p -> p.getStudent() != null && p.getStudent().getId().equals(student.getId()))
                .collect(Collectors.toList());
    }

    /** Utilise par le VOGT ADMIN. */
    public List<Payment> listAllForAdmin() {
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment createInvoice(UUID studentId, Long amountXaf, String label) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Etudiant introuvable."));
        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setAmountXaf(amountXaf);
        payment.setLabel(label);
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markPaid(UUID paymentId, String receiptUrl) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Paiement introuvable."));
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(Instant.now());
        payment.setReceiptUrl(receiptUrl);
        return paymentRepository.save(payment);
    }
}
