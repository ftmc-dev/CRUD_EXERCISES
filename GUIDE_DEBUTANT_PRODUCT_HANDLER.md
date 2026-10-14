# Comprendre le projet Product Handler, de `main.yaml` à `ProductService`

Ce guide suit le vrai chemin d'une requête dans le projet, avec des mots simples et des exemples. Imagine une petite boutique : le client demande quelque chose au guichet, le contrôleur reçoit sa demande, le service applique les règles, et le produit représente l'objet que l'on manipule.

> Note sur le nom : dans le dépôt, le fichier s'appelle `openapi/main.yaml` (et non `main.yml`). La classe de service est `src/main/java/test/productHandler/services/ProductService.java`.

## 1. La carte du projet

Le projet utilise Java 21, Maven et Spring Boot. Les grandes pièces sont :

- `openapi/main.yaml` : le plan des portes de l'API et des formes de données échangées.
- `ProductHandlerApplication` : le bouton qui démarre l'application Spring.
- `ProductController` : le guichet HTTP. Il reçoit les appels web.
- `ProductMapper` : le traducteur entre les objets de l'API et les objets internes.
- `ProductService` : l'endroit où l'on met les actions et règles liées aux produits.
- `Product` : un produit vivant dans l'application.
- `ProductPrice` et `PriceCurrency` : le prix et ses monnaies possibles.
- Les DTO générés : les objets utilisés pour parler avec le client HTTP.

Le trajet de création ressemble à ceci :

```text
Client JSON → contrat OpenAPI → ProductCreateDTO → Controller → Mapper → ProductData
            → ProductService → Product → réponse CreationResponseDTO → client JSON
```

OpenAPI Generator lit `openapi/main.yaml` pendant la phase Maven `generate-sources`. Le `pom.xml` lui demande de fabriquer des interfaces d'API et des DTO sous `target/generated-sources/openapi`. En général, on modifie le YAML, pas les fichiers générés dans `target`, car Maven peut les recréer.

## 2. Lire `openapi/main.yaml`

YAML est un format de configuration lisible. Les espaces en début de ligne montrent les niveaux, comme des boîtes imbriquées. Une ligne `clé: valeur` donne une information.

```yaml
openapi: 3.1.0
```
Dit quelle version de la notation OpenAPI décrit le contrat.

```yaml
info:
  title: Product Management API
  description: Product Management API for a Shop
  version: 1.0.0
```
`info` donne le nom, une courte description et la version de l'API. C'est l'étiquette sur le classeur de règles.

```yaml
servers:
  - url: 'https://localhost:9090'
```
Annonce une adresse de serveur. Vérifie-la avec la configuration réelle : cette ligne dit HTTPS et le port 9090, alors que le fichier `application.properties` fourni ne configure pas ce port. Le serveur local Spring utilise habituellement 8080 tant qu'on ne le change pas.

```yaml
tags:
  - name: "Product"
```
Crée une étiquette pour regrouper les opérations produit dans la documentation.

### Les chemins, donc les portes HTTP

```yaml
paths:
  /products:
```
`paths` liste les adresses que l'API comprend. `/products` est la porte de la collection de produits.

#### Créer : `POST /products`

Sous `/products`, `post:` indique une requête HTTP POST. POST sert souvent à demander la création d'une nouvelle chose.

- `tags` classe cette opération sous Product.
- `summary` et `description` expliquent brièvement son but.
- `operationId: createNewProduct` donne un nom stable à l'opération ; le générateur s'en sert pour créer une méthode Java correspondante.
- `requestBody` décrit ce que le client doit envoyer.
- `required: true` dit que le corps est obligatoire.
- `content` indique le format : `application/json`.
- `schema` dit quelle forme JSON est attendue.
- `$ref: "#/components/schemas/ProductCreateDTO"` signifie « utilise la recette ProductCreateDTO déclarée plus bas ».
- `responses` décrit les réponses possibles. `201` veut dire « créé avec succès » et son corps suit `CreationResponseDTO`.

Exemple d'entrée (forme simplifiée) :

```json
{"name":"Thé","description":"Boîte verte","price":{"monetaryAmount":1500,"currency":"XAF"}}
```

#### Lire la collection : `GET /products`

GET demande de lire. Le code 200 signifie succès. Le corps est un tableau (`type: array`) dont chaque élément a la forme `ProductDTO`. Une collection vide se représente par `[]`.

#### Lire, modifier et supprimer un seul produit

`/products/{id}` contient un morceau variable : `{id}` est remplacé par l'identifiant réel, par exemple `/products/123e...`.

- `GET` lit un produit par identifiant et renvoie `ProductDTO`.
- `PUT` demande une mise à jour complète ou largement complète. Ici l'entrée est `UpdateResponseDTO` et la réponse annoncée est `204` sans corps.
- `DELETE` demande la suppression ; `204` signifie que l'action a réussi sans contenu de réponse.
- `parameters` décrit `id`, sa place (`in: path`), le fait qu'il est obligatoire, et son type UUID.

Le contrat annonce ces opérations, mais dans le contrôleur actuel `deleteById` et `updateAllProducts` renvoient `null`, et il n'y a pas encore d'implémentation visible de lecture par id. Le contrat seul ne réalise pas le travail.

### Les schémas, les recettes des messages

`components/schemas` contient des formes réutilisables.

- `ProductCreateDTO` exige `name` et `price`. `description` est facultatif. L'identifiant et la date ne sont pas demandés au client.
- `MoneyDTO` exige `currency` et `monetaryAmount`.
- `CurrencyDTO` est une chaîne limitée à `XAF`, `USD`, `EUR`. Cette liste fermée aide à éviter des monnaies inventées comme `BANANE`.
- `CreationResponseDTO` renvoie `newId` après création.
- `ProductDTO` est la vue d'un produit renvoyée au client : `id`, `name`, `price`, `createdAt`, et éventuellement `description`.
- `UpdateResponseDTO` décrit les champs envoyés pour une mise à jour.

DTO signifie *Data Transfer Object*, objet de transfert de données. Pense à un formulaire ou un colis conçu pour un trajet. Le DTO d'entrée ne contient que ce que le client a le droit/devoir de fournir. Le DTO de sortie expose les données utiles au client. Cela évite de donner directement toute la représentation interne du produit.

## 3. Démarrage : `ProductHandlerApplication`

```java
@SpringBootApplication
public class ProductHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductHandlerApplication.class, args);
    }
}
```

- `@SpringBootApplication` dit à Spring Boot « démarre l'application et cherche ses composants dans ce paquet et ses sous-paquets ». Cette annotation regroupe notamment configuration, auto-configuration et recherche de composants.
- `public class ...` déclare la classe de démarrage.
- `main` est la porte d'entrée standard d'un programme Java.
- `String[] args` peut contenir des arguments de lancement.
- `SpringApplication.run(...)` allume Spring : il prépare les composants et le serveur web.

Analogie : `main` tourne la clé, Spring Boot ouvre la boutique et installe les guichets.

## 4. Le contrôleur : le guichet HTTP

`ProductController` porte `@RestController` et `@RequiredArgsConstructor` et implémente l'interface `ProductApi` générée depuis le YAML.

- `@RestController` indique que cette classe répond à des requêtes web et que ses retours deviennent des réponses HTTP, souvent JSON.
- `implements ProductApi` oblige la classe à fournir les opérations déclarées dans le contrat généré.
- `private final ProductService productService` est la dépendance qui effectue le travail métier.
- `private final ProductMapper productMapper` est la dépendance qui traduit les formes de données.
- `@RequiredArgsConstructor` (Lombok) génère un constructeur pour les champs `final`. Spring peut utiliser ce constructeur pour fournir les objets nécessaires : c'est l'injection par constructeur.

Dans `createNewProduct` :

1. Le client envoie un `ProductCreateDTO`.
2. Le mapper le transforme en `ProductService.ProductData`.
3. Le service crée le produit et renvoie son UUID.
4. Le contrôleur enveloppe l'UUID dans `CreationResponseDTO`.
5. `ResponseEntity.status(201).body(...)` construit une réponse HTTP 201 avec un corps JSON.

Un contrôleur devrait rester comme un guichet : comprendre la demande, déléguer, choisir la réponse HTTP. Les règles et le traitement appartiennent au service.

`getAllProduct` appelle le service, récupère une liste de `Product`, transforme chaque produit en `ProductDTO`, puis renvoie HTTP 200. La transformation utilise un stream (explication plus bas).

Les méthodes `deleteById` et `updateAllProducts` ont actuellement `return null;`. Cela signifie que ces opérations ne sont pas faites pour l'instant et qu'une requête réelle risque d'échouer. C'est un point à terminer dans un exercice CRUD.

## 5. Le mapper : le traducteur

`ProductMapper` est annoté `@Mapper(componentModel = "spring")`. MapStruct reconnaît l'interface et la rend disponible comme composant Spring. Les méthodes écrites `default` sont définies à la main, ce qui donne un contrôle clair sur les conversions.

`fromProductCreateDTOToProductData` prend le DTO d'entrée :

- elle lit le montant dans `productCreateDTO.getPrice().getMonetaryAmount()` ;
- elle convertit la monnaie du DTO vers `PriceCurrency` avec `valueOf(...)` ;
- elle fabrique un `ProductPrice` ;
- elle retourne `ProductData` avec nom, description et prix.

Les deux enums ont les mêmes noms (`XAF`, `USD`, `EUR`), donc `valueOf` peut faire la conversion. Si le nom ne correspond pas exactement, Java lance une erreur. La validation de l'entrée compte donc.

`fromProductToProductDTO` fait le chemin retour : fabrique un `MoneyDTO`, copie monnaie et montant, puis fabrique le `ProductDTO` en recopiant id, nom, description, date et prix. Le mapper isole ces conversions du contrôleur et du service.

## 6. Les objets métier

### `ProductPrice` et `PriceCurrency`

```java
public record ProductPrice(BigDecimal amount, PriceCurrency currency) { ... }
```
Un `record` est une manière courte de créer un objet principalement fait de données. Java fabrique les méthodes d'accès (`amount()`, `currency()`) et un constructeur. `BigDecimal` est adapté aux montants car les nombres décimaux flottants (`double`) peuvent introduire de petites erreurs d'arrondi.

`createProductPrice` est une méthode de fabrique : elle donne un nom parlant à l'opération de construction. Ici elle fait seulement `new ProductPrice(...)`, donc on pourrait aussi construire directement le record. Une fabrique devient plus utile lorsqu'elle vérifie des règles, par exemple refuser un montant négatif.

`PriceCurrency` est un `enum`, une liste fixe des choix permis : XAF, USD ou EUR.

### `Product`

`Product` contient `id`, `name`, `description`, `price`, `createdAt`.

- `@Getter` et `@Setter` demandent à Lombok de générer les accesseurs.
- `@NoArgsConstructor` génère un constructeur sans argument. Certains outils, notamment les frameworks de persistance, en ont besoin pour reconstruire les objets.
- Le constructeur écrit à la main reçoit `name`, `description`, `price`; puis définit ces champs et met `createdAt` à l'heure actuelle.

Pourquoi ne pas donner `id` et `createdAt` au constructeur de création ? Parce que ce sont des métadonnées gérées par le système : au moment où un nouveau produit entre, c'est l'application qui lui donne son identité et sa date. Laisser le client choisir son id pourrait créer des collisions ou usurper un autre produit. Laisser le client choisir la date de création pourrait produire une date mensongère. Pour un produit existant qu'on recharge depuis une base, un constructeur ou mécanisme de persistance séparé peut restaurer ces champs.

**Attention : l'implémentation actuelle de l'id est incorrecte pour plusieurs produits.** `private static UUID idGenerated = UUID.randomUUID();` crée un UUID unique partagé par toute la classe, puis le constructeur copie cette même valeur dans chaque nouveau produit. `static` veut dire « appartient à la classe entière », pas à chaque objet. Il faut générer un UUID par produit, par exemple `this.id = UUID.randomUUID();` dans le constructeur, ou définir le champ ainsi :

```java
private UUID id = UUID.randomUUID();
```

Il n'y a pas d'annotation JPA (`@Entity`, `@Id`) sur `Product` actuellement, donc malgré les dépendances JPA dans `pom.xml`, ce code montré conserve les objets en mémoire seulement. Une base de données demanderait une configuration et un modèle persisté adaptés.

## 7. `ProductService`, de près

Voici son idée actuelle, ligne par ligne :

```java
public class ProductService {
    List<Product> products;

    public UUID createNewProduct(ProductData productData) {
        Product product = new Product(productData.name(), productData.description(), productData.price());
        products.add(product);
        return product.getId();
    }

    public Product updateProduct(Product product) {
        return null;
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public record ProductData(String name, String description, ProductPrice price) {}
}
```

- `public class ProductService` déclare le service, l'endroit prévu pour les opérations sur les produits.
- `List<Product> products` est une liste de produits. `List` est une interface qui dit « collection ordonnée » ; une `ArrayList` est une mise en œuvre concrète fréquente.
- `createNewProduct(ProductData productData)` reçoit les données nécessaires pour créer un produit et promet de renvoyer un UUID.
- `new Product(...)` construit le produit depuis les trois données fournies.
- `products.add(product)` ajoute le produit à la liste.
- `return product.getId()` renvoie au contrôleur l'identifiant du produit créé.
- `updateProduct` est un emplacement provisoire : `return null` veut dire que la mise à jour n'est pas codée.
- `getAllProducts` renvoie la liste, pour que le contrôleur puisse la convertir en DTO.
- `record ProductData(...)` est une petite enveloppe interne avec les seules données utiles pour créer un produit.

**Deux pièces importantes manquent pour que le service fonctionne comme composant Spring :** il n'y a ni `@Service`, ni initialisation de `products`. À l'appel `products.add(...)`, `products` vaut donc `null` et provoque une `NullPointerException`. Une première version en mémoire pourrait ressembler à :

```java
@Service
public class ProductService {
    private final List<Product> products = new ArrayList<>();

    public UUID createNewProduct(ProductData data) {
        Product product = new Product(data.name(), data.description(), data.price());
        products.add(product);
        return product.getId();
    }

    public List<Product> getAllProducts() {
        return List.copyOf(products);
    }

    public record ProductData(String name, String description, ProductPrice price) {}
}
```

`@Service` permet à Spring de découvrir et construire le service. `new ArrayList<>()` crée la liste vide. `List.copyOf` renvoie une copie non modifiable pour éviter que l'appelant change directement la liste interne. Pour une vraie application, on utiliserait souvent un dépôt Spring Data JPA au lieu d'une liste mémoire : une liste disparaît quand l'application redémarre.

## 8. Pourquoi créer `ProductData` ?

`ProductData` est un record placé dans le service. Il transporte les informations de création entre contrôleur/mapper et service sans transporter tout le DTO web.

Analogie : le client remplit un formulaire coloré (DTO), le mapper lit les champs utiles et prépare une fiche de travail simple (`ProductData`) pour l'atelier (service). L'atelier n'a pas besoin de connaître le format JSON ou les classes générées par OpenAPI.

Cette séparation limite le couplage : si le contrat HTTP change, on peut souvent adapter le mapper sans réécrire les règles métier. Pour un petit exercice, on pourrait passer le DTO directement, mais cela attache le service à l'API et devient gênant à mesure que le projet grandit.

## 9. Streams : parcourir une collection

Un stream est un petit tapis roulant logique : on prend une collection, on applique des étapes, puis on récupère un résultat. Il ne stocke pas lui-même les produits et ne change pas forcément la liste d'origine.

Dans le contrôleur :

```java
List<ProductDTO> productDTOS = result.stream()
    .map(product -> productMapper.fromProductToProductDTO(product))
    .collect(Collectors.toList());
```

- `result.stream()` ouvre le tapis roulant sur chaque produit.
- `.map(...)` transforme chaque `Product` en `ProductDTO`. Le nombre d'éléments reste le même, mais leur forme change.
- `.collect(Collectors.toList())` rassemble les éléments transformés dans une nouvelle liste.

La version avec référence de méthode, montrée en commentaire dans le fichier, fait la même chose :

```java
result.stream()
    .map(productMapper::fromProductToProductDTO)
    .collect(Collectors.toList());
```

On utilise un stream quand on veut exprimer clairement une suite de transformations ou filtres sur une collection : `filter` garde certains éléments, `map` transforme, `sorted` trie, `toList` rassemble. Pour une seule action très simple, une boucle `for` est parfois plus facile à lire. Évite les streams compliqués avec beaucoup d'effets de bord : ils sont plus difficiles à comprendre et déboguer.

Exemple simple :

```java
List<String> noms = List.of("thé", "café");
List<String> majuscules = noms.stream()
    .map(nom -> nom.toUpperCase())
    .toList();
```

Résultat : `THÉ`, `CAFÉ`.

## 10. Lambdas : petites fonctions sur place

Une lambda est une petite fonction écrite là où on en a besoin. Dans `product -> ...`, le mot `product` représente un élément à la fois. La flèche veut dire « prends cet élément et fais ceci ».

```java
product -> productMapper.fromProductToProductDTO(product)
```

On peut lire : « pour chaque produit, demande au mapper son DTO ». La lambda est utilisée par `map`, qui attend une règle de transformation.

Autre exemple :

```java
n -> n * 2
```

Cela veut dire : « prends `n`, renvoie son double ». Les lambdas sont utiles avec `stream`, `filter`, `forEach`, les callbacks et les interfaces fonctionnelles. Elles évitent souvent une petite classe anonyme écrite uniquement pour une action.

Si le corps ne fait qu'appeler une méthode avec le même argument, la référence `productMapper::fromProductToProductDTO` est une forme abrégée. Commence avec la lambda, plus explicite, puis utilise la référence quand tu sais reconnaître ce motif.

## 11. `@RequiredArgsConstructor` simplement

Lombok lit les champs `final` et génère un constructeur qui les reçoit. Par exemple :

```java
@RequiredArgsConstructor
class PetitController {
    private final ProductService service;
}
```

Lombok génère conceptuellement :

```java
PetitController(ProductService service) {
    this.service = service;
}
```

Spring voit ce constructeur et fournit le service. C'est plus facile à tester et plus sûr qu'un champ qui peut rester vide. `final` signifie que la référence doit être donnée au constructeur et ne peut ensuite pas être remplacée. Dans `ProductController`, l'annotation est appropriée pour `productService` et `productMapper`.

Dans `ProductService` lui-même, si l'on ajoute un repository `final`, `@RequiredArgsConstructor` peut également demander à Spring de le fournir.

## 12. Une méthode mentale pour écrire un CRUD

Pour chaque opération, pose ces questions dans l'ordre :

1. **Quelle porte HTTP ?** POST pour créer, GET pour lire, PUT/PATCH pour modifier, DELETE pour supprimer.
2. **Quelles données entrent ?** Décris-les dans un DTO d'entrée. Ne demande pas au client les champs gérés par le serveur comme id/date de création.
3. **Qui traduit ?** Le mapper convertit les types du contrat web vers les objets du programme.
4. **Qui applique l'action ?** Le service crée, cherche, change ou supprime.
5. **Où les données vivent-elles ?** Liste pour un exercice simple ; repository/base de données pour une application persistante.
6. **Quelles données ressortent ?** Convertis vers un DTO de réponse.
7. **Quel code HTTP convient ?** 201 créé, 200 lecture réussie, 204 action réussie sans corps, 404 si l'objet demandé n'existe pas.
8. **Quelles erreurs et règles faut-il vérifier ?** UUID absent, prix invalide, champ requis manquant, etc.

Petit exemple de lecture CRUD : `GET /products` → le contrôleur appelle `service.getAllProducts()` → le service lit les produits → le mapper transforme chaque produit → le contrôleur répond 200 avec une liste JSON.

## 13. Ce qu'il faut retenir de l'état actuel

La structure pédagogique est bonne pour apprendre les rôles séparés (contrat, contrôleur, mapper, service, modèle). Le code est encore incomplet : service non annoté `@Service`, liste non initialisée, UUID partagé à cause du `static`, et méthodes update/delete non implémentées. Le contrat mentionne aussi une lecture par id qui n'apparaît pas encore dans le contrôleur visible. Corrige ces points un par un pendant les exercices, et vérifie le trajet complet d'une requête à chaque étape.
