package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByProductName(String productName);

    List<Product> findByProductNameContaining(String keyword);

    Page<Product> findByProductNameContaining(String keyword, Pageable pageable);

    List<Product> findByCategory(Category category);

    Page<Product> findByCategory(Category category, Pageable page);

    List<Product> findAllByActiveStatus(ActiveStatus activeStatus);

    Page<Product> findByProductNameContainingIgnoreCaseAndActiveStatus(String keyword, ActiveStatus activeStatus,
            Pageable pageable);
}
