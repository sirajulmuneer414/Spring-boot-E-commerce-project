package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.product.Brand;
import com.sirajul.lenscraft.entity.product.enums.BrandStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Integer> {
    boolean existsByBrandNameIgnoreCase(String brandName);

    List<Brand> findAllByBrandStatus(BrandStatus brandStatus);
}
