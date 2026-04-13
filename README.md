# SONABEL-Info : Plateforme Communautaire de Suivi Électrique

## 📝 Sujet du projet
**SONABEL-Info** est une application collaborative basée sur le **crowdsourcing**. Elle repose sur la participation active des citoyens pour cartographier l'état du réseau électrique en temps réel au Burkina Faso, tout en offrant un canal de communication officiel à la nationale d'électricité.

L'essence du projet s'articule autour de deux acteurs clés :
1. **Les Citoyens :** Un utilisateur signale une coupure ou un retour de courant dans sa zone, et la communauté valide ou non cette information.
2. **La SONABEL (Intervention Officielle) :** En cas de travaux programmés ou de maintenance, la SONABEL utilise les canaux de l'application pour communiquer directement avec les abonnés concernés, rendant l'information officielle plus accessible.

### ✨ La "Touche Particulière"
Notre projet se distingue par un **système de score de fiabilité dynamique**. Chaque utilisateur possède un profil dont la crédibilité évolue en fonction de la véracité de ses signalements. Si vos informations sont confirmées par vos voisins, votre indice de fiabilité augmente ; s'elles sont contestées, il diminue. Cela transforme l'application en un véritable réseau de confiance.

---

## 👥 Membres du groupe
* **Membre 1 :** Ilboudo Issouf 
* **Membre 2 :** N'DO Jean Amaud

---

## 🏗 Architecture choisie
L'application repose sur une architecture **Client-Serveur** moderne intégrant les composants suivants :

* **Frontend (Android/Java) :** Utilisation de `RecyclerView` pour l'affichage dynamique des listes, de `Material Design` pour l'interface utilisateur et de `BottomNavigationView` pour la navigation.
* **Backend (Firebase) :**
    * **Firebase Auth :** Pour la gestion sécurisée des comptes (Agents et Abonnés).
    * **Realtime Database :** Base de données NoSQL utilisée pour synchroniser instantanément les annonces et les signalements.
* **Logique de Fiabilité :** Implémentation d'un verrou par ID utilisateur (`UID`) pour empêcher le spam de votes (confirmation/contestation unique) et calcul dynamique du taux de fiabilité des informateurs.

---

## 🚀 Fonctionnalités Clés
- **Authentification :** Inscription et connexion sécurisées.
- **Dashboard Agent :** Interface dédiée à la gestion et publication d'annonces classées par types (Maintenance, Extension, Incident).
- **Interface Utilisateur :**
    * Flux d'annonces officielles avec badges de statut (En cours, Programmé).
    * Historique des signalements récents.
    * Profil personnel dynamique (Statistiques de signalements, date d'adhésion et score de fiabilité).
- **Système de Votes :** Possibilité de confirmer ou contester un signalement pour nettoyer les fausses informations.

---

## ⚙️ Installation et Test

### Prérequis
* Android Studio (version Ladybug ou plus récente).
* Un appareil Android (physique ou émulateur) tournant sous API 24+.
* Une connexion Internet active.

### Étapes d'installation
1.  **Clonage du dépôt :**
    ```bash
    git clone 
    ```
2.  **Ouverture du projet :** Ouvrez Android Studio et sélectionnez le dossier du projet.
3.  **Configuration Firebase :**
    * Assurez-vous que le fichier `google-services.json` est présent dans le répertoire `app/`.
    * (Note : Le projet est déjà lié à notre console Firebase de test).
4.  **Synchronisation Gradle :** Cliquez sur "Sync Project with Gradle Files" et attendez la fin du processus.

### Procédure de Test
1.  **Lancement :** Exécutez l'application sur votre émulateur ou téléphone.
2.  **Connexion Agent :** Connectez-vous avec un compte agent pour publier une annonce dans la section "Mes Annonces".
3.  **Connexion Utilisateur :** Créez un compte utilisateur et vérifiez que l'annonce de l'agent apparaît instantanément.
4.  **Signalement :** Effectuez un signalement de délestage et demandez à un autre utilisateur de le confirmer pour voir votre score de fiabilité évoluer sur votre profil.

---

## 🛠 Outils utilisés
* **Langage :** Java
* **Interface :** XML / Material Components
* **Base de données :** Firebase Realtime Database
* **Gestion de version :** Git / GitHub
