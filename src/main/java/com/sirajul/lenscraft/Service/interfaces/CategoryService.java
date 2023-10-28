package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.entity.product.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAllCategories();

    Category findCategoryById(Long categoryId);

    void addCategory(Category category);
}
