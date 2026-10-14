# 5 exercices CRUD progressifs avec Spring

Ces exercices servent à pratiquer les opérations CRUD : créer (*Create*), lire (*Read*), modifier (*Update*) et supprimer (*Delete*). Ils sont classés du plus facile au plus complexe.

Pour chaque exercice, le but est de réaliser le comportement demandé et de vérifier qu'il fonctionne. Les choix d'organisation et les outils de stockage restent libres : aucun repository n'est requis.

## Exercice 1 — Une liste de tâches très simple

### Objectif

Créer une petite API qui permet de gérer des tâches à faire.

### Données d'une tâche

Chaque tâche contient :

- un identifiant unique ;
- un titre ;
- une indication disant si la tâche est terminée.

### Ce qu'il faut réaliser

- Ajouter une tâche avec un titre.
- Afficher toutes les tâches.
- Afficher une tâche à partir de son identifiant.
- Modifier le titre ou l'état « terminée » d'une tâche.
- Supprimer une tâche à partir de son identifiant.

### Critères de réussite

- Une nouvelle tâche reçoit un identifiant.
- Une tâche inconnue ne doit pas être modifiée ou supprimée comme si elle existait.
- Après une modification, la lecture de la tâche montre les nouvelles valeurs.
- Après une suppression, la tâche n'apparaît plus dans la liste.
- Un titre manquant ou vide est refusé.

## Exercice 2 — Un carnet de contacts

### Objectif

Gérer des contacts et permettre de retrouver facilement une personne.

### Données d'un contact

Chaque contact contient :

- un identifiant unique ;
- un prénom ;
- un nom ;
- une adresse e-mail ;
- un numéro de téléphone, facultatif.

### Ce qu'il faut réaliser

- Créer un contact.
- Afficher tous les contacts.
- Afficher un contact par son identifiant.
- Rechercher des contacts par prénom ou nom, sans tenir compte des majuscules et minuscules.
- Modifier les informations d'un contact.
- Supprimer un contact.

### Critères de réussite

- Le prénom, le nom et l'adresse e-mail sont obligatoires.
- L'adresse e-mail doit avoir une forme valide.
- Une recherche sans résultat renvoie une liste vide.
- La recherche fonctionne même si la casse utilisée ne correspond pas exactement.
- Modifier un contact ne doit pas créer un deuxième contact.

## Exercice 3 — Un catalogue de livres

### Objectif

Créer un catalogue qui peut être consulté et filtré.

### Données d'un livre

Chaque livre contient :

- un identifiant unique ;
- un titre ;
- un auteur ;
- une année de publication ;
- un genre ;
- une indication de disponibilité.

### Ce qu'il faut réaliser

- Ajouter un livre.
- Afficher tous les livres.
- Afficher un livre par son identifiant.
- Filtrer les livres par auteur, genre ou disponibilité.
- Modifier les informations d'un livre.
- Supprimer un livre.

### Critères de réussite

- Le titre et l'auteur sont obligatoires.
- L'année de publication doit être une année plausible et ne doit pas être dans le futur.
- Plusieurs filtres peuvent être utilisés ensemble.
- Un filtre absent ne doit pas empêcher l'affichage des autres résultats.
- Une recherche qui ne trouve rien renvoie une liste vide.

## Exercice 4 — Un mini catalogue de produits

### Objectif

Gérer les produits d'une boutique et proposer plusieurs critères de recherche.

### Données d'un produit

Chaque produit contient :

- un identifiant unique ;
- un nom ;
- une description facultative ;
- un prix ;
- une catégorie ;
- une quantité en stock ;
- une date de création.

### Ce qu'il faut réaliser

- Créer un produit.
- Afficher tous les produits.
- Afficher un produit par son identifiant.
- Rechercher les produits par nom ou catégorie.
- Afficher les produits dont le prix se trouve dans une fourchette donnée.
- Modifier les informations d'un produit.
- Supprimer un produit.

### Critères de réussite

- Le nom, le prix et la catégorie sont obligatoires.
- Le prix ne peut pas être négatif.
- La quantité en stock ne peut pas être négative.
- La date de création est renseignée automatiquement lors de la création.
- Le client ne choisit pas l'identifiant ni la date de création.
- Lors d'une mise à jour, l'identifiant et la date de création restent ceux du produit existant.
- Les filtres de nom, catégorie et prix peuvent être combinés.

## Exercice 5 — Gestion de réservations de salles

### Objectif

Gérer des salles et leurs réservations en respectant les règles de disponibilité.

### Données d'une salle

Chaque salle contient :

- un identifiant unique ;
- un nom ;
- une capacité maximale ;
- une indication disant si elle peut être réservée.

### Données d'une réservation

Chaque réservation contient :

- un identifiant unique ;
- l'identifiant de la salle ;
- le nom de la personne qui réserve ;
- une date et une heure de début ;
- une date et une heure de fin ;
- le nombre de participants.

### Ce qu'il faut réaliser

- Créer, consulter, modifier et supprimer des salles.
- Créer une réservation pour une salle.
- Afficher une réservation par son identifiant.
- Afficher les réservations d'une salle.
- Afficher les réservations comprises dans une période donnée.
- Modifier ou annuler une réservation.

### Critères de réussite

- Une réservation doit viser une salle existante et réservable.
- Le nombre de participants doit être positif et ne peut pas dépasser la capacité de la salle.
- L'heure de fin doit être après l'heure de début.
- Deux réservations d'une même salle ne peuvent pas se chevaucher.
- Deux réservations de salles différentes peuvent avoir lieu en même temps.
- Une modification de réservation doit aussi respecter toutes les règles de disponibilité.
- Une réservation inconnue ou une salle inconnue est signalée clairement.
- La suppression d'une salle qui possède des réservations doit suivre une règle cohérente définie pour l'exercice : refuser la suppression ou supprimer également ses réservations.

## Conseils pour vérifier chaque exercice

Sans choisir une méthode particulière, vérifie au minimum les situations suivantes :

- création correcte ;
- lecture d'un élément existant ;
- lecture d'un élément inconnu ;
- modification correcte ;
- suppression correcte ;
- données invalides ;
- liste vide ou recherche sans résultat ;
- vérification que les changements apparaissent dans les lectures suivantes.

Tu peux commencer par l'exercice 1 et passer au suivant lorsque tous ses critères de réussite sont satisfaits.
