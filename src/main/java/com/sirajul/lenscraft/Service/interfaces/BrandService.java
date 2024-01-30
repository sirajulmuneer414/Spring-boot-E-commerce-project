package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.product.Brand;

import java.util.List;

public interface BrandService {
    List<Brand> findAllBrands();

    void addBrand(String brandName);

    Brand findBrandById(Integer brandId);

    boolean existByBrandName(String brandName);
}
