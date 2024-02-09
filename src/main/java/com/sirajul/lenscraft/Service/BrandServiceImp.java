package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.Repository.BrandRepository;
import com.sirajul.lenscraft.Repository.ProductRepository;
import com.sirajul.lenscraft.Repository.UserRepository;
import com.sirajul.lenscraft.Service.interfaces.BrandService;
import com.sirajul.lenscraft.entity.product.Brand;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.enums.BrandStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImp implements BrandService {
    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<Brand> findAllBrands() {
        return brandRepository.findAll();

    }

    @Override
    public void addBrand(String brandName) {
        Brand brand = new Brand();
        brand.setBrandName(brandName);
        brand.setBrandStatus(BrandStatus.ACTIVE);

        brandRepository.save(brand);
    }

    @Override
    public Brand findBrandById(Integer brandId) {
        return brandRepository.findById(brandId).get();
    }

    @Override
    public boolean existByBrandName(String brandName) {
       return brandRepository.existsByBrandNameIgnoreCase(brandName);
    }

    @Override
    public List<Brand> findAllBrandsActive() {
        return brandRepository.findAllByBrandStatus(BrandStatus.ACTIVE);
    }

    @Override
    public void update(Brand prod) {

        brandRepository.save(prod);

    }

    @Override
    public void saveProductFromBrand(Product product) {
        productRepository.save(product);
    }

    @Override
    public void deleteById(Integer brandId) {
        brandRepository.deleteById(brandId);
    }
}
