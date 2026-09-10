# Déployer VOGT Digital Campus en ligne (sans rien installer en local)

Important, avant de commencer : **je n'ai pas d'accès internet dans cet
environnement** — je ne peux pas pousser votre code sur GitHub ni cliquer sur
les boutons de Render/Netlify à votre place. Ce guide est écrit pour que vous
puissiez le faire vous-même en 15-20 minutes, uniquement depuis votre
navigateur (pas de terminal, pas de Java, pas de Docker à installer).

Il vous faut seulement un compte **GitHub** (gratuit) pour héberger le code —
c'est l'étape la plus "technique" et elle se fait aussi depuis le navigateur.

## Vue d'ensemble

| Composant | Hébergeur recommandé | Pourquoi |
|---|---|---|
| Backend (Spring Boot) | **Render** (render.com) | Build direct depuis le `Dockerfile` fourni, PostgreSQL managé gratuit inclus |
| Frontend (HTML/JS statique) | **Netlify** (netlify.com) | Glisser-déposer le dossier `vogt-frontend`, en ligne en 1 minute |
| Kafka | À reporter | Voir note plus bas — l'app démarre sans, vous pourrez l'ajouter ensuite |

## Étape 1 — Mettre le code sur GitHub

1. Créez un compte sur https://github.com si vous n'en avez pas.
2. Cliquez sur **New repository**, nommez-le `vogt-digital-campus-backend`, laissez-le **Public** ou **Private**, ne cochez rien d'autre, cliquez **Create repository**.
3. Sur la page du repo vide, cliquez **uploading an existing file**, puis glissez-déposez tout le contenu du dossier `vogt-backend` (décompressé) que je vous ai livré. Validez le commit.
4. Répétez l'opération pour un second repo `vogt-digital-campus-frontend` avec le contenu du dossier `vogt-frontend`.

*(Alternative si vous préférez : `git clone` + `git push` depuis un terminal — mais l'upload web suffit.)*

## Étape 2 — Déployer le backend sur Render

1. Créez un compte sur https://render.com (connexion possible directement avec GitHub).
2. **New +** → **PostgreSQL**. Nom : `vogt-db`. Plan gratuit. Créez-la, puis notez l'**Internal Database URL** affichée sur sa page (commence par `postgres://...`).
3. **New +** → **Web Service** → connectez votre repo `vogt-digital-campus-backend`.
4. Render détecte le `Dockerfile` automatiquement — laissez **Environment: Docker**.
5. Dans **Environment Variables**, ajoutez (valeurs à adapter) :
   - `DB_URL` = `jdbc:postgresql://<host interne de vogt-db>:5432/<nom_db>` (Render affiche ces infos séparément — hôte, port, nom, utilisateur, mot de passe — reconstituez l'URL JDBC avec, ou utilisez le bouton "Connect" de Render qui vous les donne)
   - `DB_USER` = l'utilisateur fourni par Render
   - `DB_PASSWORD` = le mot de passe fourni par Render
   - `JWT_SECRET` = une chaîne aléatoire d'au moins 32 caractères (générez-en une, ex. via https://generate-secret.vercel.app/32)
   - `CORS_ORIGINS` = l'URL de votre futur site Netlify (vous la connaîtrez après l'étape 3 — vous pourrez revenir la modifier)
   - `PUBLIC_BASE_URL` = l'URL de **ce service Render lui-même** (ex. `https://vogt-backend.onrender.com`) — **indispensable**, sinon les images uploadées (galerie, logos) resteront invisibles sur le site
   - `KAFKA_BROKERS` = laissez la valeur par défaut pour l'instant (voir note Kafka plus bas)
6. Cliquez **Create Web Service**. Render construit l'image Docker et démarre l'application (comptez 3-5 minutes la première fois).
7. Une fois "Live", votre API est disponible à une URL du type `https://vogt-digital-campus-backend.onrender.com/api/v1`. Testez avec `https://.../api/v1/swagger-ui.html`.

## Étape 3 — Déployer le frontend sur Netlify

1. Avant de déployer, éditez **une seule ligne** dans chaque fichier HTML du frontend : ajoutez juste avant `<script src="js/api.js"></script>` :
   ```html
   <script>window.VOGT_API_BASE_URL = "https://VOTRE-URL-RENDER.onrender.com/api/v1";</script>
   ```
   (remplacez par l'URL obtenue à l'étape 2.6). Faites ce changement directement dans l'éditeur de fichiers GitHub (icône crayon sur chaque fichier), pas besoin d'outil local.
2. Créez un compte sur https://netlify.com (connexion possible avec GitHub).
3. **Add new site** → **Import an existing project** → choisissez votre repo `vogt-digital-campus-frontend`.
4. Aucune commande de build n'est nécessaire (site statique) : laissez "Build command" vide et "Publish directory" à `.` (racine).
5. Cliquez **Deploy**. Votre site est en ligne en moins d'une minute, à une URL du type `https://vogt-digital-campus.netlify.app`.
6. Retournez sur Render (étape 2.5) et mettez à jour `CORS_ORIGINS` avec cette URL Netlify exacte, puis redéployez le service backend (bouton "Manual Deploy").

## Étape 4 — Créer votre premier compte administrateur

Le seed livré crée un compte `admin@vogthightech.cm` de démarrage, mais il vaut
mieux en créer un propre en production :

1. Ouvrez `https://VOTRE-URL-RENDER.onrender.com/api/v1/swagger-ui.html`.
2. Utilisez `POST /api/v1/auth/register/candidate` pour créer un premier utilisateur (le rôle par défaut est CANDIDATE).
3. Connectez-vous à la base PostgreSQL via l'onglet **Shell** de Render (ou un client comme TablePlus/DBeaver avec l'External Database URL) et changez manuellement le `role` de cet utilisateur en `SUPER_ADMIN` :
   ```sql
   UPDATE users SET role = 'SUPER_ADMIN' WHERE email = 'votre.email@exemple.com';
   ```
4. Connectez-vous ensuite sur `https://votre-site.netlify.app/admin-login.html` avec cet email.

## Note sur Kafka

L'application **démarre normalement sans Kafka disponible** — Spring Kafka
retente la connexion en arrière-plan et journalise des erreurs, mais cela
n'empêche pas l'API de répondre. Vous pouvez donc tester tout le reste
(formations, candidatures, actualités, portails) sans Kafka pour l'instant.

Quand vous voudrez activer les notifications automatiques :
- Solution la plus simple à ajouter plus tard : **Upstash Kafka** (https://upstash.com), compatible avec le protocole Kafka, offre un plan gratuit, se configure uniquement via variables d'environnement (`KAFKA_BROKERS`, plus les identifiants SASL qu'Upstash fournit — il faudra alors ajouter la config de sécurité SASL dans `application.yml`, dites-moi quand vous y êtes et je l'ajoute).

## Limites de cette itération à garder en tête

- Pas encore de stockage S3 réel pour les documents (l'upload fonctionne mais écrit sur le disque du conteneur Render, qui **n'est pas persistant** entre redéploiements — acceptable pour tester, pas pour la production réelle).
- Pas de nom de domaine personnalisé configuré (vous pouvez en ajouter un dans Render et Netlify une fois prêt).
- Le compte de seed doit être supprimé ou son mot de passe changé après vos tests.

Dites-moi si vous bloquez sur une étape précise (je peux détailler celle-ci en particulier), ou si vous préférez que je vous prépare la configuration pour un autre hébergeur (Railway, Fly.io, VPS...).
