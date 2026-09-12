# Déploiement du correctif des images partenaires

1. Déployer ce backend sur Render.
2. Vérifier que `PUBLIC_BASE_URL=https://vogt-backend.onrender.com`.
3. Attendre que le service soit `Live`.
4. Ouvrir :
   `https://vogt-backend.onrender.com/api/v1/public/partners`
5. Les `logoUrl` doivent maintenant être absolues, par exemple :
   `https://vogt-backend.onrender.com/files/partners/....jpg`
6. Ouvrir ensuite le site Netlify et recharger.

Ce correctif normalise aussi les anciens enregistrements déjà stockés avec `/files/...`; aucune modification manuelle de la base de données n'est nécessaire pour ces URLs.
