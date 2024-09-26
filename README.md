# Projet Etudiant Openclassrooms n°5 – Concevoir une application web Java de A à Z



<img src="/preview.jpg" alt="Logo de l'application">

<h1 align="center">Pay My Buddy</h1>

PayMyBuddy est une application web qui permet aux utilisateurs de gérer leurs relations et d'effectuer des paiements à leurs amis.

<br>

## Fonctionnalités

- Inscription et authentification des utilisateurs
- Gestion des relations utilisateur (ajout, validation, suppression)
- Transactions de paiement entre utilisateurs
- Modification des informations de l'utilisateur


## Technologies

- **Java 21** - Langage de programmation.
- **Spring Boot 3.3.1** - Framework Java pour créer des applications web.
- **PostgreSQL** - Systeme de gestion de base de données relationnelle.
- **Gradle** - Outil de build automatisé.
- **Thymeleaf** - Moteur de template pour la génération de pages HTML.
- **Tailwind CSS** - Framework CSS pour la conception de l'interface utilisateur.

<br>

## Installation

<br>

1. Cloner le dépôt :
    ```sh
    git clone https://github.com/Xenophee/Pay-My-Buddy.git
    ```

<br>

2. Créer une base de données PostgreSQL pay_my_buddy et y inséré dans l'ordre les scripts SQL suivants contenus dans le dossier `src/main/resources` :
    - `bdd.sql`
    - `data.sql`
    - `triggers.sql`

<br>
   
3. Configurer la base de données dans `src/main/resources/application.properties` :
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/pay_my_buddy?currentSchema=dev
    spring.datasource.username=yourusername
    spring.datasource.password=yourpassword
    ```

<br>

4. Faire de même avec le fichier `src/main/resources/application-test.properties` pour les tests.

<br>

5. Construire le projet :
    ```sh
    ./gradlew build
    ```
<br>

6. Lancer l'application :
    ```sh
    ./gradlew bootRun
    ```

<br>
7. Ouvrir un navigateur et accéder à l'URL `http://localhost:8080`.

<br>
<br>

## Tests

<br>

Pour exécuter les tests avec Gradle :
```sh
./gradlew test
```

Retrouvez le rapport de test dans ` build/reports/tests/test/index.html.` et le rapport de couverture de test Jacoco dans le dossier ` build/reports/jacoco/test/html/index.html.`.


<br>

## Structure de la base de données

