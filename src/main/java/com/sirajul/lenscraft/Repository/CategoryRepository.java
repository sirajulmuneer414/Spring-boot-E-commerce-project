package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {


    boolean existsByCategoryNameIgnoreCase(String categoryName);

    List<Category> findAllByCategoryStatus(CategoryStatus name);
}
