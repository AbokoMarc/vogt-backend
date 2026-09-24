package cm.vogt.digitalcampus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * VOGT HIGH TECH — Digital Campus
 * Ecole superieure de technologie et d'ingenierie, une ecole de l'INUCASTY.
 *
 * Principe directeur : ZERO HARDCODE.
 * Aucune donnee institutionnelle (formations, frais, dates, effectifs, contenus)
 * n'est codee en dur : tout transite par la base de donnees et est administrable
 * depuis le futur back-office (VOGT CMS / VOGT ADMIN).
 */
@SpringBootApplication
public class VogtApplication {
    public static void main(String[] args) {
        SpringApplication.run(VogtApplication.class, args);
    }
}
