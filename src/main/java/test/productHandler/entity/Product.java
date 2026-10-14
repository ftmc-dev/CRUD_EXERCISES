package test.productHandler.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class Product {
   private static UUID idGenerated= UUID.randomUUID();
    private final UUID id ;
    private String name;
    private String description;
    private ProductPrice price;
    private OffsetDateTime createdAt;

    public Product(String name, String description, ProductPrice price) {
        this.id = idGenerated;
        this.name = name;
        this.description = description;
        this.price = price;
        this.createdAt = OffsetDateTime.now();
    }
}
