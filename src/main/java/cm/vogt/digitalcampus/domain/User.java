package cm.vogt.digitalcampus.domain;

import cm.vogt.digitalcampus.common.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String firstName;
    private String lastName;
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean active = true;
    private boolean twoFactorEnabled = false;
    private String twoFactorSecret;

    /** Trace qui a cree ce compte (email) — utilise pour la hierarchie admin, jamais expose aux roles limites. */
    private String createdByEmail;

    private String passwordResetTokenHash;
    private java.time.Instant passwordResetExpiry;
}
