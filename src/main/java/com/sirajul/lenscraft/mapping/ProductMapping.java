package com.sirajul.lenscraft.mapping;

import com.sirajul.lenscraft.DTO.Product.ProductDto;
import com.sirajul.lenscraft.Repository.BrandRepository;
import com.sirajul.lenscraft.Repository.CategoryRepository;
import com.sirajul.lenscraft.entity.product.Brand;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.enums.FrameSize;
import com.sirajul.lenscraft.entity.product.enums.StockStatus;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductMapping {

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    CategoryRepository categoryRepository;

    public Product DtoToProduct(ProductDto productDto){

        Product product = new Product();
        if(productDto.getProductId() != null){
            product.setProductId(productDto.getProductId());
        }
        product.setHavingOffer(false);
        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        switch (productDto.getStockStatus()){
            case "OUT_OF_STOCK":
                product.setStockStatus(StockStatus.OUT_OF_STOCK);
                break;
            case "LOW_STOCK":
                product.setStockStatus(StockStatus.LOW_STOCK);
                break;

            default:
                product.setStockStatus(StockStatus.IN_STOCK);
                break;

        }
        switch (productDto.getFrameSize()){
            case "SMALL":
                product.setFrameSize(FrameSize.SMALL);
                break;
            case "MEDIUM":
                product.setFrameSize(FrameSize.MEDIUM);
                break;
            case "LARGE":
                product.setFrameSize(FrameSize.LARGE);
                break;
        }

        Brand brand = brandRepository.findById(productDto.getBrandId()).get();
        product.setBrand(brand);

        Category category = categoryRepository.findById(productDto.getCategoryId()).get();
        product.setCategory(category);

        product.setModelNo(productDto.getModelNo());

        product.setDiscountedPrice(productDto.getPrice());

        product.setActiveStatus(ActiveStatus.ACTIVE);

        return product;
    }

    public ProductDto ProductToDto(Product product) {
        ProductDto productDto = new ProductDto();

        productDto.setProductId(product.getProductId());
        productDto.setProductName(product.getProductName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setBrandId(product.getBrand().getBrandId());
        productDto.setModelNo(product.getModelNo());
        productDto.setStockStatus(product.getStockStatus().name());
        productDto.setFrameSize(product.getFrameSize().name());
        productDto.setFrameLength(product.getFrameSize().getFrameLength());
        productDto.setCategoryId(product.getCategory().getCategoryId());
        productDto.setVariables(product.getVariables());
        productDto.setOffer(product.getOffer());
        productDto.setDiscountedPrice(product.getDiscountedPrice());

        return productDto;
    }
}
