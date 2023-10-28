package com.sirajul.lenscraft.Repository;

import com.sirajul.lenscraft.entity.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {



}
