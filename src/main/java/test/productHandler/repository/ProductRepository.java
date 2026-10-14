package test.productHandler.repository;

import org.springframework.stereotype.Repository;
import test.productHandler.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(products.get(id));
    }

    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    public void deleteById(UUID id) {
        products.remove(id);
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public boolean existsById(UUID id) {
        return products.containsKey(id);
    }
}
