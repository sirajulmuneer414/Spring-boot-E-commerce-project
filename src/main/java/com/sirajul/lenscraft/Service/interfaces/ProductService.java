package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.DTO.Product.ProductDto;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import org.springframework.data.domain.Page;
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


    List<Product> findAllProductsContaining(String keyword);

    Page<Product> findAllProductsInPageable(int pageNo, int pageSize);

    Page<Product> findAllProductsContaining(String keyword, int pageNo, int pageSize);

    int totalPagesCount(int pageSize);

    List<Product> findAllProductByCategory(Category category);

    Page<Product> findAllProductByCategory(Category category, int pageNo, int pageSize);

    void blockById(Long productId);

    void unBlockById(Long productId);

    Page<Product> findAllProductsContainingActive(String keyword, int pageNo, int pageSize);

    Page<Product> findAllProductsInPageableActive(int pageNo, int pageSize);
}
