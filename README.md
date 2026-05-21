# Employee Management System - Android App

Une application Android moderne pour la gestion des employés, intégrant des statistiques salariales et une synchronisation en temps réel via une API PHP.

## 🚀 Fonctionnalités

- **Gestion des Employés** : Lister, ajouter, modifier et supprimer des employés.
- **Recherche Avancée** : Barre de recherche intégrée avec filtrage en temps réel (Debounce).
- **Statistiques Visuelles** : Visualisation des salaires (Minimum, Maximum, Total) via des graphiques (BarChart).
- **Architecture Robuste** : Utilisation de Retrofit pour les appels API et ViewBinding pour l'interface.
- **Gestion d'États** : Pattern `UiState` pour gérer les chargements et les erreurs proprement.

## 🛠 Technologies utilisées

- **Langage** : Kotlin
- **Interface** : XML / Material Design 3
- **Réseau** : Retrofit 2 & OkHttp
- **Graphiques** : MPAndroidChart
- **Backend** : PHP (API REST)
- **Base de données** : MySQL (via PHP)

## 📋 Prérequis

1. **Serveur Web** : Un serveur local type XAMPP, WAMP ou MAMP.
2. **Base de données** :
   - Créer une base nommée `androiddb`.
   - Créer la table `Employe` avec les colonnes : `numEmp` (PK), `nom`, `nbr_jour`, `taux_journalier`.
3. **API PHP** : Placer le fichier `api.php` dans votre dossier `htdocs` (ex: `htdocs/Connection/api.php`).

## ⚙️ Configuration du projet

### 1. Backend (PHP)
Assurez-vous que l'URL dans `RetrofitClient.kt` correspond à l'adresse IP de votre machine :
```kotlin
private const val BASE_URL = "http://192.168.x.x/Connection/"
```

### 2. Permissions
L'application requiert la permission Internet (déjà configurée dans le Manifest) :
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 📸 Captures d'écran

*(Ajoutez vos captures d'écran ici)*

## 📄 Licence

Ce projet est sous licence libre.
