package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.DTO.Product.ProductDto;
import com.sirajul.lenscraft.entity.product.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<Product> findAllProducts();

    Product saveProductAndGetProduct(ProductDto productDto, List<MultipartFile> image1, List<MultipartFile> image2, List<MultipartFile> image3);

    List<String> getFrameColorsById(Product product);

    void deleteById(Long productId);

    Product findProductById(Long productId);

    void updateProduct(ProductDto productDto);

    void save(Product product);


}
