package test.productHandler.services;

import org.springframework.stereotype.Service;
import test.productHandler.entity.Product;
import test.productHandler.entity.ProductPrice;
import test.productHandler.exceptions.ProductNotFoundException;
import test.productHandler.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    List<Product> products;
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public UUID createNewProduct(ProductData productData) {
        Product product = new Product(productData.name(), productData.description(), productData.price());
        productRepository.save(product);
        return product.getId();
    }
 public List<Product> getAllProducts() {
        return productRepository.findAll();
 }

    public void deleteById(UUID id) {
        if(!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product with id: " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    public Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
    }

    public record ProductData(String name, String description, ProductPrice price){}
}
