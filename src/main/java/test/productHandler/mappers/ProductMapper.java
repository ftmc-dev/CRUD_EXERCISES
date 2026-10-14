package test.productHandler.mappers;

import org.mapstruct.Mapper;
import test.productHandler.dto.UpdateResponseDTO;
import test.productHandler.entity.PriceCurrency;
import test.productHandler.entity.Product;
import test.productHandler.entity.ProductPrice;
import test.productHandler.services.ProductService;
import test.productHandler.dto.CurrencyDTO;
import test.productHandler.dto.MoneyDTO;
import test.productHandler.dto.ProductCreateDTO;
import test.productHandler.dto.ProductDTO;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    default ProductService.ProductData fromProductCreateDTOToProductData(ProductCreateDTO productCreateDTO){
        ProductPrice productPrice = ProductPrice.createProductPrice(
                productCreateDTO.getPrice().getMonetaryAmount(),
                PriceCurrency.valueOf(productCreateDTO.getPrice().getCurrency().name())
        );

       return new ProductService.ProductData(productCreateDTO.getName(), productCreateDTO.getDescription(), productPrice);
    }

    default ProductDTO fromProductToProductDTO(Product product) {
        MoneyDTO moneyDTO = new MoneyDTO();
        moneyDTO.setCurrency(CurrencyDTO.valueOf(product.getPrice().currency().name()));
        moneyDTO.setMonetaryAmount(product.getPrice().amount());

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setCreatedAt(product.getCreatedAt());
        productDTO.setPrice(moneyDTO);
        return productDTO;
    }

    default ProductService.ProductData fromUpdateAllResponseDTOToProductData(UpdateResponseDTO updateResponseDTO) {
        ProductPrice updateProductPrice = ProductPrice.updateProductPrice(
                updateResponseDTO.getPrice().getMonetaryAmount(),
                PriceCurrency.valueOf(updateResponseDTO.getPrice().getCurrency().name())
        );

        return new ProductService.ProductData(updateResponseDTO.getName(), updateResponseDTO.getDescription(), updateProductPrice);
    }
}
