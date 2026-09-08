package cm.vogt.digitalcampus.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Abonnement Web Push d'un appareil (etudiant/enseignant/admin) — permet
 *  d'envoyer de vraies notifications systeme, meme app/onglet ferme. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "push_subscriptions")
public class PushSubscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 1000)
    private String endpoint;

    private String p256dh;
    private String auth;
}
