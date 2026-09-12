package cm.vogt.digitalcampus.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sans ce module, Jackson essaie de serialiser les proxies Hibernate (relations
 * @ManyToOne/@OneToMany chargees en lazy) et plante avec une erreur du type
 * "No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor".
 *
 * Ce module apprend a Jackson a reconnaitre un proxy Hibernate et a le serialiser
 * proprement : si la relation n'a pas encore ete chargee, elle est simplement
 * omise (null) au lieu de faire planter toute la reponse. C'est la cause exacte
 * des erreurs "erreur interne" rencontrees sur la soumission de candidature,
 * l'upload de documents et la creation de factures — tous ces endpoints
 * renvoient des entites JPA directement.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        Hibernate6Module module = new Hibernate6Module();
        // Ne force jamais le chargement des relations non initialisees (evite du N+1
        // et des erreurs de session fermee) — elles sont simplement omises.
        module.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        module.configure(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        return module;
    }
}
