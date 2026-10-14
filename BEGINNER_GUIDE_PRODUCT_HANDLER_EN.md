# Understanding the Product Handler Project, from `main.yaml` to `ProductService`

This guide follows the real path of a request through the project, using simple words and examples. Imagine a small shop: a customer makes a request at the counter, the controller receives it, the service does the work, and the product is the thing being handled.

> File name note: in this repository, the file is `openapi/main.yaml` (not `main.yml`). The service class is `src/main/java/test/productHandler/services/ProductService.java`.

## 1. The project map

This project uses Java 21, Maven, and Spring Boot. Its main pieces are:

- `openapi/main.yaml`: the plan for the API doors and the shapes of the data exchanged.
- `ProductHandlerApplication`: the button that starts the Spring application.
- `ProductController`: the HTTP counter. It receives web requests.
- `ProductMapper`: the translator between API objects and internal objects.
- `ProductService`: the place for product-related actions and rules.
- `Product`: a product used inside the application.
- `ProductPrice` and `PriceCurrency`: the price and its allowed currencies.
- Generated DTOs: objects used to communicate with the HTTP client.

The create request travels like this:

```text
Client JSON → OpenAPI contract → ProductCreateDTO → Controller → Mapper → ProductData
            → ProductService → Product → CreationResponseDTO response → Client JSON
```

OpenAPI Generator reads `openapi/main.yaml` during Maven's `generate-sources` phase. The `pom.xml` tells it to create API interfaces and DTOs under `target/generated-sources/openapi`. Usually, edit the YAML rather than generated files under `target`, because Maven can recreate those files.

## 2. Reading `openapi/main.yaml`

YAML is a readable configuration format. Indentation shows which items belong inside other items, like nested boxes. A line in the form `key: value` gives information.

```yaml
openapi: 3.1.0
```
This says which OpenAPI notation version describes the contract.

```yaml
info:
  title: Product Management API
  description: Product Management API for a Shop
  version: 1.0.0
```
`info` gives the API's name, a short description, and its version. Think of it as the label on a folder of rules.

```yaml
servers:
  - url: 'https://localhost:9090'
```
This announces a server address. Compare it with the real configuration: this line says HTTPS and port 9090, but the provided `application.properties` does not configure that port. A local Spring server normally uses port 8080 unless you change it.

```yaml
tags:
  - name: "Product"
```
This creates a label to group product operations in the documentation.

### Paths: the HTTP doors

```yaml
paths:
  /products:
```
`paths` lists the addresses the API understands. `/products` is the door to the product collection.

#### Create: `POST /products`

Under `/products`, `post:` describes an HTTP POST request. POST is often used to ask for something new to be created.

- `tags` groups this operation under Product.
- `summary` and `description` briefly explain its purpose.
- `operationId: createNewProduct` gives the operation a stable name; the generator uses it to create a matching Java method.
- `requestBody` describes what the client must send.
- `required: true` says the request body is mandatory.
- `content` specifies the format: `application/json`.
- `schema` says what shape of JSON is expected.
- `$ref: "#/components/schemas/ProductCreateDTO"` means “use the ProductCreateDTO recipe declared below.”
- `responses` describes possible responses. `201` means “created successfully,” and its body follows the `CreationResponseDTO` shape.

Example input (simplified):

```json
{"name":"Tea","description":"Green box","price":{"monetaryAmount":1500,"currency":"XAF"}}
```

#### Read the collection: `GET /products`

GET asks to read data. Code 200 means success. The body is an array (`type: array`) whose items have the `ProductDTO` shape. An empty collection is represented by `[]`.

#### Read, update, and delete one product

`/products/{id}` contains a variable part: `{id}` is replaced by the real identifier, for example `/products/123e...`.

- `GET` reads one product by id and returns `ProductDTO`.
- `PUT` asks for a full or broad update. Here, the input is `UpdateResponseDTO` and the declared response is `204` with no body.
- `DELETE` asks to remove the product; `204` means the action succeeded without response content.
- `parameters` describes `id`, where it appears (`in: path`), whether it is required, and its type (UUID).

The contract declares these operations, but in the current controller `deleteById` and `updateAllProducts` return `null`, and there is no visible implementation for reading by id. The contract describes the work; it does not perform it by itself.

### Schemas: the recipes for messages

`components/schemas` contains reusable shapes.

- `ProductCreateDTO` requires `name` and `price`. `description` is optional. The client does not send the id or creation time.
- `MoneyDTO` requires `currency` and `monetaryAmount`.
- `CurrencyDTO` is a string limited to `XAF`, `USD`, or `EUR`. This closed list helps prevent invented currencies such as `BANANA`.
- `CreationResponseDTO` returns `newId` after creation.
- `ProductDTO` is the view of a product returned to the client: `id`, `name`, `price`, `createdAt`, and optional `description`.
- `UpdateResponseDTO` describes the fields sent for an update.

DTO means *Data Transfer Object*. Think of it as a form or package designed for a particular trip. The input DTO contains only what the client is allowed or expected to provide. The output DTO exposes the data useful to the client. This avoids handing the internal product representation directly to the client.

## 3. Startup: `ProductHandlerApplication`

```java
@SpringBootApplication
public class ProductHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductHandlerApplication.class, args);
    }
}
```

- `@SpringBootApplication` tells Spring Boot, “start the application and look for components in this package and its subpackages.” This annotation combines configuration, auto-configuration, and component scanning.
- `public class ...` declares the startup class.
- `main` is the standard entry point of a Java program.
- `String[] args` can contain startup arguments.
- `SpringApplication.run(...)` turns Spring on: it prepares components and the web server.

Analogy: `main` turns the key; Spring Boot opens the shop and sets up the counters.

## 4. The controller: the HTTP counter

`ProductController` has `@RestController` and `@RequiredArgsConstructor`, and implements the `ProductApi` interface generated from the YAML.

- `@RestController` says this class handles web requests and its return values become HTTP responses, often JSON.
- `implements ProductApi` requires the class to provide the operations declared in the generated contract.
- `private final ProductService productService` is the dependency that does the business work.
- `private final ProductMapper productMapper` is the dependency that translates data shapes.
- `@RequiredArgsConstructor` (from Lombok) generates a constructor for the `final` fields. Spring can use that constructor to provide the required objects: this is constructor injection.

In `createNewProduct`:

1. The client sends a `ProductCreateDTO`.
2. The mapper transforms it into `ProductService.ProductData`.
3. The service creates the product and returns its UUID.
4. The controller wraps the UUID in `CreationResponseDTO`.
5. `ResponseEntity.status(201).body(...)` builds an HTTP 201 response with a JSON body.

A controller should be like a counter clerk: understand the request, delegate the work, and choose the HTTP response. Business rules and processing belong in the service.

`getAllProduct` calls the service, gets a list of `Product` objects, transforms each into a `ProductDTO`, and returns HTTP 200. The transformation uses a stream (explained below).

The `deleteById` and `updateAllProducts` methods currently have `return null;`. That means these operations have not been implemented yet, and a real request may fail. These are things to complete in a CRUD exercise.

## 5. The mapper: the translator

`ProductMapper` is annotated with `@Mapper(componentModel = "spring")`. MapStruct recognizes the interface and makes it available as a Spring component. The methods written as `default` are implemented by hand, giving clear control over the conversions.

`fromProductCreateDTOToProductData` takes the input DTO:

- It reads the amount using `productCreateDTO.getPrice().getMonetaryAmount()`.
- It converts the DTO currency to `PriceCurrency` with `valueOf(...)`.
- It creates a `ProductPrice`.
- It returns `ProductData` containing the name, description, and price.

Both enums have the same names (`XAF`, `USD`, `EUR`), so `valueOf` can convert between them. If a name does not match exactly, Java throws an error. Input validation is therefore important.

`fromProductToProductDTO` goes the other way: it creates a `MoneyDTO`, copies the currency and amount, then creates a `ProductDTO` and copies the id, name, description, date, and price. The mapper keeps these conversions out of the controller and service.

## 6. The domain objects

### `ProductPrice` and `PriceCurrency`

```java
public record ProductPrice(BigDecimal amount, PriceCurrency currency) { ... }
```
A `record` is a short way to create an object mainly made of data. Java creates accessor methods (`amount()`, `currency()`) and a constructor. `BigDecimal` is suitable for money because floating-point decimal numbers (`double`) can introduce small rounding errors.

`createProductPrice` is a factory method: it gives the construction operation a descriptive name. Here it only calls `new ProductPrice(...)`, so the record could also be created directly. A factory becomes more useful when it checks rules, such as rejecting a negative amount.

`PriceCurrency` is an `enum`, a fixed list of allowed choices: XAF, USD, or EUR.

### `Product`

`Product` contains `id`, `name`, `description`, `price`, and `createdAt`.

- `@Getter` and `@Setter` ask Lombok to generate accessor methods.
- `@NoArgsConstructor` generates a constructor with no arguments. Some tools, especially persistence frameworks, need one to rebuild objects.
- The hand-written constructor receives `name`, `description`, and `price`; then it sets those fields and sets `createdAt` to the current time.

Why not pass `id` and `createdAt` to the creation constructor? Because these are system-managed metadata: when a new product arrives, the application assigns its identity and creation time. Letting the client choose an id could cause collisions or let it impersonate another product. Letting the client choose the creation date could create a false date. For an existing product loaded from a database, a separate constructor or persistence mechanism can restore those fields.

**Warning: the current id implementation is wrong when creating multiple products.** `private static UUID idGenerated = UUID.randomUUID();` creates one UUID shared by the whole class, and the constructor copies that same value into every product. `static` means “belongs to the class as a whole,” not “belongs to each object.” Generate one UUID per product instead, for example with `this.id = UUID.randomUUID();` in the constructor, or declare the field like this:

```java
private UUID id = UUID.randomUUID();
```

There is no JPA annotation (`@Entity`, `@Id`) on `Product` currently, so despite the JPA dependencies in `pom.xml`, the shown code keeps objects in memory only. A database would need suitable persistence configuration and a persistent model.

## 7. `ProductService`, up close

Here is its current idea, line by line:

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

- `public class ProductService` declares the service, the intended place for product operations.
- `List<Product> products` is a list of products. `List` is an interface meaning “ordered collection”; `ArrayList` is a common concrete implementation.
- `createNewProduct(ProductData productData)` receives the data needed to create a product and promises to return a UUID.
- `new Product(...)` builds a product from the three provided values.
- `products.add(product)` adds the product to the list.
- `return product.getId()` gives the controller the id of the created product.
- `updateProduct` is a placeholder: `return null` means the update has not been coded.
- `getAllProducts` returns the list so the controller can convert it to DTOs.
- `record ProductData(...)` is a small internal wrapper containing only the data needed to create a product.

**Two important pieces are missing for this service to work as a Spring component:** there is no `@Service`, and `products` is not initialized. So when `products.add(...)` runs, `products` is `null` and causes a `NullPointerException`. A first in-memory version could look like this:

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

`@Service` lets Spring discover and create the service. `new ArrayList<>()` creates an empty list. `List.copyOf` returns an unmodifiable copy so callers cannot directly change the internal list. In a real application, a Spring Data JPA repository is often used instead of an in-memory list: a list disappears when the application restarts.

## 8. Why create `ProductData`?

`ProductData` is a record inside the service. It carries creation information from the controller/mapper to the service without carrying the whole web DTO.

Analogy: the client fills in a colorful form (DTO), the mapper reads the useful fields and prepares a simple work slip (`ProductData`) for the workshop (service). The workshop does not need to know about JSON formatting or classes generated by OpenAPI.

This separation reduces coupling: if the HTTP contract changes, you can often adapt the mapper without rewriting business rules. For a small exercise, you could pass the DTO directly, but that ties the service to the API and becomes inconvenient as the project grows.

## 9. Streams: processing a collection

A stream is like a small logical conveyor belt: you take a collection, apply steps, and then gather a result. A stream does not store the products itself and does not necessarily change the original list.

In the controller:

```java
List<ProductDTO> productDTOS = result.stream()
    .map(product -> productMapper.fromProductToProductDTO(product))
    .collect(Collectors.toList());
```

- `result.stream()` starts the conveyor belt over each product.
- `.map(...)` transforms each `Product` into a `ProductDTO`. The number of items stays the same, but their shape changes.
- `.collect(Collectors.toList())` gathers the transformed items into a new list.

The method reference version, shown in a comment in the file, does the same thing:

```java
result.stream()
    .map(productMapper::fromProductToProductDTO)
    .collect(Collectors.toList());
```

Use a stream when you want to express a clear sequence of transformations or filters on a collection: `filter` keeps selected items, `map` transforms, `sorted` sorts, and `toList` gathers the results. For one very simple action, a `for` loop can be easier to read. Avoid complicated streams with many side effects: they are harder to understand and debug.

Simple example:

```java
List<String> names = List.of("tea", "coffee");
List<String> uppercase = names.stream()
    .map(name -> name.toUpperCase())
    .toList();
```

Result: `TEA`, `COFFEE`.

## 10. Lambdas: small functions written in place

A lambda is a small function written where it is needed. In `product -> ...`, the word `product` represents one item at a time. The arrow means “take this item and do this.”

```java
product -> productMapper.fromProductToProductDTO(product)
```

Read it as: “for each product, ask the mapper for its DTO.” `map` uses the lambda as a transformation rule.

Another example:

```java
n -> n * 2
```

This means: “take `n` and return double its value.” Lambdas are useful with `stream`, `filter`, `forEach`, callbacks, and functional interfaces. They often avoid writing a small anonymous class just to perform one action.

If the body only calls a method with the same argument, the method reference `productMapper::fromProductToProductDTO` is a shorter form. Start with the lambda, which is more explicit, and use the reference once you recognize the pattern.

## 11. `@RequiredArgsConstructor`, simply

Lombok reads the `final` fields and generates a constructor that receives them. For example:

```java
@RequiredArgsConstructor
class SmallController {
    private final ProductService service;
}
```

Lombok generates something like:

```java
SmallController(ProductService service) {
    this.service = service;
}
```

Spring sees this constructor and provides the service. This is easier to test and safer than a field that might remain empty. `final` means the reference must be provided by the constructor and cannot be replaced later. In `ProductController`, the annotation is suitable for `productService` and `productMapper`.

In `ProductService` itself, if you add a `final` repository, `@RequiredArgsConstructor` can also ask Spring to provide it.

## 12. A mental checklist for writing CRUD

For each operation, ask these questions in order:

1. **Which HTTP door?** POST to create, GET to read, PUT/PATCH to update, DELETE to remove.
2. **What data comes in?** Describe it in an input DTO. Do not ask the client for fields managed by the server, such as id or creation time.
3. **Who translates it?** The mapper converts web-contract types into program objects.
4. **Who performs the action?** The service creates, finds, changes, or removes the product.
5. **Where does the data live?** A list for a simple exercise; a repository/database for persistent applications.
6. **What data goes out?** Convert it to a response DTO.
7. **Which HTTP status fits?** 201 created, 200 successful read, 204 successful action with no body, 404 if the requested object does not exist.
8. **Which errors and rules must be checked?** Missing UUID, invalid price, missing required field, and so on.

A simple CRUD reading example: `GET /products` → the controller calls `service.getAllProducts()` → the service reads the products → the mapper transforms each product → the controller responds with 200 and a JSON list.

## 13. What to remember about the current code

The teaching structure is good for learning the separate roles (contract, controller, mapper, service, model). The code is still incomplete: the service is not annotated `@Service`, its list is not initialized, the UUID is shared because of `static`, and update/delete are not implemented. The contract also mentions reading by id, which is not yet visible in the controller. Fix these points one at a time as you work through the exercises, and check the complete request path at each step.
