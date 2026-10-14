package test.productHandler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import test.productHandler.dto.UpdateProductRequestDTO;
import test.productHandler.entity.Product;
import test.productHandler.mappers.ProductMapper;
import test.productHandler.services.ProductService;
import test.productHandler.api.ProductApi;
import test.productHandler.dto.ProductCreateDTO;
import test.productHandler.dto.ProductDTO;
import test.productHandler.dto.UpdateResponseDTO;
import test.productHandler.dto.CreationResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductApi {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @Override
    public ResponseEntity<CreationResponseDTO> createNewProduct(ProductCreateDTO productCreateDTO) {
        ProductService.ProductData productData = productMapper.fromProductCreateDTOToProductData(productCreateDTO);
        UUID result = productService.createNewProduct(productData);
        return ResponseEntity.status(201).body(new CreationResponseDTO(result));
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        productService.deleteById(id);
        return null;
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getAllProduct() {
        List<Product> result = productService.getAllProducts();
        List<ProductDTO> productDTOS = result.stream()
                .map(productMapper::fromProductToProductDTO)
                .collect(Collectors.toList());

        return ResponseEntity.status(200).body(productDTOS);
    }

    @Override
    public ResponseEntity<ProductDTO> getById(UUID id) {
        Product product = productService.getById(id);
        ProductDTO productDTO = productMapper.fromProductToProductDTO(product);
        return ResponseEntity.status(200).body(productDTO);
    }

    @Override
    public ResponseEntity<Void> updateAllProducts(UUID id, UpdateProductRequestDTO updateProductRequestDTO) {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateProduct(UUID id, UpdateResponseDTO updateResponseDTO) {

        return null;
    }
}


