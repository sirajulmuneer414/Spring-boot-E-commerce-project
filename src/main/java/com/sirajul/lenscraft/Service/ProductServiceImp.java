package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.DTO.Product.ProductDto;
import com.sirajul.lenscraft.Repository.BrandRepository;
import com.sirajul.lenscraft.Repository.CategoryRepository;
import com.sirajul.lenscraft.Repository.ProductRepository;
import com.sirajul.lenscraft.Repository.VariablesRepository;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.entity.product.Brand;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.mapping.ProductMapping;
import com.sirajul.lenscraft.utils.FileUploadUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImp implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductMapping productMapping;

    @Autowired
    VariablesRepository variablesRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC,"productId"));
    }

    @Override
    public Product saveProductAndGetProduct(ProductDto productDto, List<MultipartFile> image1, List<MultipartFile> image2, List<MultipartFile> image3) {


       productRepository.save(productMapping.DtoToProduct(productDto));

        Product product = productRepository.findByProductName(productDto.getProductName());

        String uploadDir = "lenscraft/src/main/resources/static/productImages/"+product.getProductId();

        for(MultipartFile image : image1){
            String filename = StringUtils.cleanPath(image.getOriginalFilename());
            try {
                FileUploadUtil.saveFile(uploadDir,filename,image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for(MultipartFile image : image2){
            String filename = StringUtils.cleanPath(image.getOriginalFilename());
            try {
                FileUploadUtil.saveFile(uploadDir,filename,image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for(MultipartFile image : image3){
            String filename = StringUtils.cleanPath(image.getOriginalFilename());
            try {
                FileUploadUtil.saveFile(uploadDir,filename,image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return product;
    }

    @Override
    public List<String> getFrameColorsById(Product product) {


        return variablesRepository.findFrameColorByProduct(product);
    }

    @Override
    @Transactional
    public void deleteById(Long productId) {
        Product product = productRepository.findById(productId).get();
        Brand brand = brandRepository.findById(product.getBrand().getBrandId()).get();

        brand.getProducts().remove(product);

        brandRepository.save(brand);

        Category category = categoryRepository.findById(product.getCategory().getCategoryId()).get();

        category.getProducts().remove(product);

        categoryRepository.save(category);

        for(Variables variables : product.getVariables()){

        variablesRepository.delete(variables);
        }
        product.setVariables(new ArrayList<>());
        productRepository.save(product);

        productRepository.delete(product);
    }

    @Override
    public Product findProductById(Long productId) {
        return productRepository.findById(productId).get();
    }

    @Override
    public void updateProduct(ProductDto productDto) {
        productRepository.save(productMapping.DtoToProduct(productDto));


    }

    @Override
    public void save(Product product) {
        productRepository.save(product);

    }

    @Override
    public List<Product> findAllProductsContaining(String keyword) {
        return productRepository.findByProductNameContaining(keyword);
    }

    @Override
    public Page<Product> findAllProductsInPageable(int pageNo, int pageSize) {
        Pageable page = PageRequest.of(pageNo-1,pageSize,Sort.by(Sort.Direction.DESC,"productId"));
        return productRepository.findAll(page);
    }

    @Override
    public Page<Product> findAllProductsContaining(String keyword, int pageNo, int pageSize) {

            // Create a pageable object to support pagination
            Pageable pageable = PageRequest.of(pageNo, pageSize);

            // Fetch products based on the provided keyword and pageable information
            List<Product> products = productRepository.findByProductNameContaining(keyword);

            // Return the page with products and pageable information
            return new PageImpl<>(products, pageable, products.size());
        }


    @Override
    public int totalPagesCount(int pageSize) {
       int pageCount = (int)productRepository.count()/pageSize;

        return pageCount;
    }
}
