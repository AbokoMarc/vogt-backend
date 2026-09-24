package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.BadRequestException;
import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.common.enums.Role;
import cm.vogt.digitalcampus.domain.*;
import cm.vogt.digitalcampus.dto.accounts.*;
import cm.vogt.digitalcampus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Toute la logique de hierarchie des comptes vit ici, jamais dans les
 * controleurs : c'est le seul endroit qui doit connaitre les regles de
 * visibilite (un ADMIN ne doit jamais voir qu'un SUPER_ADMIN existe).
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private static final Set<Role> SUPER_ADMIN_CAN_CREATE = Set.of(
            Role.ADMIN, Role.ADMISSIONS_OFFICER, Role.ACADEMIC_ADMIN, Role.COMMUNICATION_ADMIN);
    private static final Set<Role> ADMIN_CAN_CREATE = Set.of(
            Role.ADMISSIONS_OFFICER, Role.ACADEMIC_ADMIN, Role.COMMUNICATION_ADMIN);
    private static final Set<Role> SUB_ADMIN_ROLES = ADMIN_CAN_CREATE;

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ProgramRepository programRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final AuditLogService auditLogService;

    /* ================= Enseignants ================= */

    @Transactional
    public void createTeacher(CreateTeacherRequest request, String creatorEmail) {
        User user = createBaseUser(request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhone(), request.getInitialPassword(), Role.TEACHER, creatorEmail);

        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setDepartment(request.getDepartment());
        teacher.setTitle(request.getTitle());
        teacher.setSpecialties(request.getSpecialties());
        teacherRepository.save(teacher);

        notifyNewAccount(user, "enseignant");
        auditLogService.log("CREATE_TEACHER", "User", user.getId().toString(), "Compte enseignant cree : " + user.getEmail());
    }

    /* ================= Etudiants ================= */

    @Transactional
    public void createStudent(CreateStudentRequest request, String creatorEmail) {
        User user = createBaseUser(request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhone(), request.getInitialPassword(), Role.STUDENT, creatorEmail);

        Student student = new Student();
        student.setUser(user);
        student.setMatricule(request.getMatricule());
        student.setYearOfStudy(request.getYearOfStudy());
        if (request.getProgramId() != null) {
            Program program = programRepository.findById(request.getProgramId())
                    .orElseThrow(() -> new NotFoundException("Formation introuvable."));
            student.setProgram(program);
        }
        studentRepository.save(student);

        notifyNewAccount(user, "étudiant");
        auditLogService.log("CREATE_STUDENT", "User", user.getId().toString(), "Compte etudiant cree : " + user.getEmail());
    }

    @Transactional
    public void setAccountActive(UUID userId, boolean active) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Compte introuvable."));
        user.setActive(active);
        userRepository.save(user);
        auditLogService.log(active ? "REACTIVATE_ACCOUNT" : "DEACTIVATE_ACCOUNT", "User", user.getId().toString(),
                user.getEmail() + " (" + user.getRole() + ")");
    }

    public List<Student> listStudents() { return studentRepository.findAll(); }
    public List<Teacher> listTeachers() { return teacherRepository.findAll(); }

    /* ================= Comptes admin — hierarchie discrete ================= */

    @Transactional
    public void createAdminAccount(CreateAdminAccountRequest request, Role creatorRole, String creatorEmail) {
        Role targetRole;
        try {
            targetRole = Role.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Role invalide.");
        }

        boolean allowed = (creatorRole == Role.SUPER_ADMIN && SUPER_ADMIN_CAN_CREATE.contains(targetRole))
                || (creatorRole == Role.ADMIN && ADMIN_CAN_CREATE.contains(targetRole));

        if (!allowed) {
            throw new BadRequestException("Vous n'êtes pas autorisé à créer un compte avec ce rôle.");
        }

        User user = createBaseUser(request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhone(), request.getInitialPassword(), targetRole, creatorEmail);

        notifyNewAccount(user, "administration");
        auditLogService.log("CREATE_ADMIN_ACCOUNT", "User", user.getId().toString(),
                "Compte " + targetRole + " cree par " + creatorEmail);
    }

    /**
     * Liste des comptes admin visibles par le demandeur.
     * SUPER_ADMIN voit tout (ADMIN + sous-admins), avec qui a cree quoi.
     * ADMIN ne voit QUE les sous-admins (jamais un autre ADMIN, jamais le SUPER_ADMIN)
     * — c'est cette regle qui garantit que le SUPER_ADMIN reste invisible.
     */
    public List<AccountSummaryResponse> listAdminAccounts(Role requesterRole) {
        var allUsers = userRepository.findAll();
        Set<Role> visibleRoles = (requesterRole == Role.SUPER_ADMIN)
                ? Set.of(Role.ADMIN, Role.ADMISSIONS_OFFICER, Role.ACADEMIC_ADMIN, Role.COMMUNICATION_ADMIN)
                : SUB_ADMIN_ROLES;

        return allUsers.stream()
                .filter(u -> visibleRoles.contains(u.getRole()))
                .map(u -> new AccountSummaryResponse(
                        u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getRole().name(),
                        u.isActive(),
                        requesterRole == Role.SUPER_ADMIN ? u.getCreatedByEmail() : null, // masque le createur pour un simple ADMIN
                        u.getCreatedAt()
                ))
                .toList();
    }

    /* ================= Utilitaires ================= */

    private User createBaseUser(String firstName, String lastName, String email, String phone,
                                 String initialPassword, Role role, String creatorEmail) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Un compte existe deja avec cet email.");
        }
        String passwordToUse = (initialPassword == null || initialPassword.isBlank())
                ? generateSecurePassword()
                : initialPassword;

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(passwordToUse));
        user.setRole(role);
        user.setActive(true);
        user.setCreatedByEmail(creatorEmail);
        return userRepository.save(user);
    }

    /** Mot de passe temporaire aleatoire — jamais affiche a l'administrateur ;
     *  la personne definit son propre mot de passe via le lien recu par email. */
    private String generateSecurePassword() {
        byte[] bytes = new byte[18];
        new java.security.SecureRandom().nextBytes(bytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void notifyNewAccount(User user, String espace) {
        emailService.send(
                user.getEmail(),
                "Votre compte VOGT HIGH TECH a été créé",
                "Bonjour " + user.getFirstName() + ",\n\nUn compte " + espace + " a été créé pour vous sur le Digital Campus de VOGT HIGH TECH.\n" +
                        "Email : " + user.getEmail() + "\n\n" +
                        "Pour des raisons de sécurité, définissez votre propre mot de passe via le lien que vous allez recevoir séparément.\n\n" +
                        "VOGT HIGH TECH — Une école de l'INUCASTY"
        );
        // Envoie egalement un lien de definition de mot de passe (reutilise le flux "mot de passe oublie").
        passwordService.requestReset(user.getEmail());
    }
}
