# VOGT HIGH TECH — Digital Campus (Backend)

Backend Spring Boot du **Digital Campus** de VOGT HIGH TECH, école supérieure
de technologie et d'ingénierie — une école de l'INUCASTY (Yaoundé, Cameroun).

Construit avec le même stack que vos autres projets (TransCam, Fintech CEMAC) :
**Spring Boot + PostgreSQL + Kafka**, pour rester cohérent avec votre façon de
faire plutôt que de repartir sur Next.js/NestJS comme proposé dans la première
version du cahier des charges.

## Principe directeur : ZERO HARDCODE

Aucune donnée institutionnelle (formations, frais, dates de concours, effectifs,
enseignants, laboratoires, partenaires, contenus de pages) n'est écrite en dur
dans le code. Tout est administrable via l'API `/api/v1/admin/**` (le futur
VOGT ADMIN / VOGT CMS), stocké en base et versionné par année académique.

## Stack

| Composant | Choix |
|---|---|
| Langage / Framework | Java 17, Spring Boot 3.3 |
| Base de données | PostgreSQL |
| Messagerie évènementielle | Apache Kafka |
| Sécurité | Spring Security + JWT (access + refresh token), RBAC par rôle |
| Documentation API | springdoc-openapi (Swagger UI) |
| Build | Maven |

## Architecture des packages

```
cm.vogt.digitalcampus
├── config/          Sécurité, CORS, JPA auditing, OpenAPI, topics Kafka
├── security/        JWT, UserDetails, filtre d'authentification
├── common/          ApiResponse, PageResponse, exceptions, gestion globale des erreurs
├── domain/          Entités JPA (User, Program, Application, Student, News, Event, ...)
├── repository/      Spring Data JPA
├── dto/             Requêtes / réponses par module (auth, program, application, content)
├── service/         Logique métier (candidatures, catalogue formations, actualités...)
├── controller/      REST API — /api/v1/public, /api/v1/candidates, /api/v1/admin
└── kafka/           Producer/consumer + événements (statut candidature, notifications)
```

## Rôles RBAC (cf. cahier des charges section "ROLES")

`SUPER_ADMIN`, `ADMIN`, `ADMISSIONS_OFFICER`, `ACADEMIC_ADMIN`,
`COMMUNICATION_ADMIN`, `TEACHER`, `STUDENT`, `CANDIDATE`, `ALUMNI`, `PARTNER`.

Les permissions fines sont posées via `@PreAuthorize` sur chaque contrôleur
admin (ex. seuls `ACADEMIC_ADMIN` et les admins peuvent publier une formation).

## Entité centrale : AcademicYear

Chaque formation, frais, événement peut être rattaché à une `AcademicYear`.
Activer une nouvelle année (`POST /api/v1/admin/academic-years/{id}/activate`)
archive automatiquement l'année précédente — **vous ne redéployez jamais de
code pour un changement d'année académique.**

## Modules couverts dans cette V1

- **Auth** : inscription candidat, connexion, JWT access + refresh
- **Catalogue formations** (`Program`, `Specialization`, `Course`) — CRUD admin + lecture publique filtrée par catégorie
- **Admissions** : création de candidature, génération du numéro de suivi `VHT-{année}-{séquence}`, soumission, changement de statut (bureau des admissions), suivi candidat
- **Étudiants / Enseignants / Alumni** : entités + repositories prêts, à brancher aux portails dédiés
- **Actualités (News)** : éditeur avec brouillon / publication / programmation / archivage
- **Événements** : création, publication, liste des événements à venir
- **Vogt Labs, Projets étudiants, Partenaires, Galerie, FAQ** : entités CMS prêtes (modules activables/désactivables)
- **Paiements (Payment)** : structure de base pour la scolarité, montants toujours lus depuis `Program`
- **Notifications + Kafka** : `vogt.application.status-changed`, `vogt.notification.requested`, `vogt.content.published`
- **Audit log** : entité prête pour tracer les actions d'administration
- **Dashboard admin** : `/api/v1/admin/dashboard/overview` (effectifs réels, pas de faux chiffres)

## Modules ajoutés dans cette itération (backend complet)

- **Portail étudiant** (`/api/v1/students/me/**`) : profil, emploi du temps, notes, présences, paiements
- **Portail enseignant** (`/api/v1/teachers/me/**`) : cours assignés, saisie de notes, saisie de présences
- **Emploi du temps, Notes, Présences** (`Timetable`, `Grade`, `Attendance`) — entités + repositories + endpoints
- **CMS complet** pour Vogt Labs, Partenaires, Galerie, FAQ, Projets étudiants, Alumni (endpoints admin + public)
- **Paiements / scolarité** : création de facture, marquage payé côté admin, consultation côté étudiant — montants toujours issus de `Program`, jamais d'un chiffre en dur
- **Upload de documents** avec `FileStorageService` : implémentation locale (dev) **et implémentation S3-compatible réelle** (`S3FileStorageService`, AWS SDK v2) — bascule automatique selon `STORAGE_PROVIDER`
- **Liste admin des candidatures** (`GET /api/v1/admin/applications`) avec infos candidat/formation/statut
- **Journal d'audit réel** (`AuditLogService`) : chaque publication/dépublication/archivage de formation ou article, chaque activation d'année académique, chaque changement de statut de candidature est tracé — consultable via `GET /api/v1/admin/audit-logs` (SUPER_ADMIN)
- **Email SMTP réel** (`EmailService`, JavaMailSender) branché sur les notifications de candidature — désactivé par défaut (`MAIL_ENABLED=false`, journalisé seulement), s'active en renseignant les identifiants SMTP
- **Recherche globale** (`GET /api/v1/public/search?q=...`) sur formations, actualités, FAQ
- **2FA (TOTP)** pour les comptes administrateurs — implémentation RFC 6238 pure Java, compatible Google Authenticator/Authy (`/api/v1/admin/2fa/setup|enable|disable`), vérifié automatiquement au login si activé

## Modules encore à compléter selon vos besoins

- Adaptateur WhatsApp réel (nécessite des identifiants Meta Business API ou Twilio — l'événement Kafka `NOTIFICATION_REQUESTED` est prêt à recevoir cet adaptateur)
- i18n FR/EN (contenu institutionnel bilingue — nécessite des champs de traduction sur chaque entité de contenu)
- Chatbot "Vogt AI" (doit uniquement lire les données validées via l'API — jamais inventer d'information)
- Visite virtuelle 360° (nécessite des médias réels de l'établissement)
- SEO avancé : le frontend actuel est un site statique qui charge son contenu en JavaScript (SPA-like), ce qui limite l'indexation par les moteurs de recherche des pages dynamiques (formation.html, actualite.html). Pour un vrai référencement, il faudra soit du rendu côté serveur (SSR), soit une génération statique des pages de formation/actualité au moment de leur publication.

## Démarrage local

```bash
cp .env.example .env
# renseigner les vraies valeurs dans .env

docker compose up -d          # Postgres + Kafka
export $(cat .env | xargs)    # ou utiliser un plugin dotenv de votre IDE
mvn spring-boot:run
```

L'API est disponible sur `http://localhost:8080/api/v1`.
Documentation interactive : `http://localhost:8080/api/v1/swagger-ui.html`.

## Seed de démarrage

`src/main/resources/db/seed/seed.sql` crée uniquement :
- une année académique active neutre (`2026-2027`)
- un compte `SUPER_ADMIN` de démarrage (mot de passe à changer immédiatement)

**Aucune formation, aucun tarif, aucune actualité n'est pré-remplie** —
conformément à la règle « ne jamais inventer d'information institutionnelle ».
C'est à l'administration de créer le contenu réel depuis l'API admin une fois
en ligne.

## Exemple de flux : candidature en ligne

```bash
# 1. Le candidat crée son compte
POST /api/v1/auth/register/candidate

# 2. Il se connecte
POST /api/v1/auth/login

# 3. Il crée sa candidature (formation 1er choix)
POST /api/v1/candidates/applications
Authorization: Bearer {accessToken}

# 4. Il soumet son dossier
POST /api/v1/candidates/applications/{trackingNumber}/submit

# 5. Le bureau des admissions fait avancer le statut
PATCH /api/v1/admin/applications/{trackingNumber}/status
{ "status": "UNDER_REVIEW" }

# 6. Le candidat suit sa candidature
GET /api/v1/candidates/applications/{trackingNumber}
```

Chaque changement de statut publie un événement Kafka
(`vogt.application.status-changed`) qui génère une notification — même
principe événementiel que sur TransCam.

## Sécurité

- Mots de passe hashés en BCrypt (jamais en clair)
- JWT signé HMAC-SHA256, secret fourni uniquement par variable d'environnement
- CORS restreint aux origines déclarées dans `CORS_ORIGINS`
- RBAC appliqué au niveau méthode (`@EnableMethodSecurity`)
- Gestion d'erreurs centralisée (`GlobalExceptionHandler`) — aucune fuite de stacktrace côté client
- Prévu pour 2FA administrateurs (`User.twoFactorEnabled`, logique à implémenter selon le fournisseur choisi)

## Prochaine étape suggérée

1. Brancher un frontend (le mockup HTML déjà livré peut être transformé en
   client consommant cette API) sur `/api/v1/public/**`.
2. Renseigner les vraies données (formations, frais 2026–2027, enseignants,
   coordonnées) via l'API admin — jamais dans le code.
3. Ajouter le stockage S3-compatible réel pour les documents candidats et la galerie.
