package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Product findByProductName(String productName);

}
