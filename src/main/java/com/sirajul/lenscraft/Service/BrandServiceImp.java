package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.Repository.BrandRepository;
import com.sirajul.lenscraft.Service.interfaces.BrandService;
import com.sirajul.lenscraft.entity.product.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImp implements BrandService {
    @Autowired
    BrandRepository brandRepository;

    @Override
    public List<Brand> findAllBrands() {
        return brandRepository.findAll();

    }

    @Override
    public void addBrand(String brandName) {
        Brand brand = new Brand();
        brand.setBrandName(brandName);

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
}
