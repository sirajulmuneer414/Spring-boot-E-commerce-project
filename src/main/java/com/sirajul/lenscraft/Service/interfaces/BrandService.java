package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.product.Brand;
import com.sirajul.lenscraft.entity.product.Product;

import java.util.List;

public interface BrandService {
    List<Brand> findAllBrands();

    void addBrand(String brandName);

    Brand findBrandById(Integer brandId);

    boolean existByBrandName(String brandName);

    List<Brand> findAllBrandsActive();

    void update(Brand prod);

    void saveProductFromBrand(Product product);

    void deleteById(Integer brandId);
}
