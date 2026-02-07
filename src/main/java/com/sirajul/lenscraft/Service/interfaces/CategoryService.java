package com.sirajul.lenscraft.Service.interfaces;

import com.sirajul.lenscraft.DTO.shop.CategoryHomeDto;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;

import java.util.List;

public interface CategoryService {
    List<Category> findAllCategories();

    Category findCategoryById(Long categoryId);

    void addCategory(Category category);

    List<CategoryHomeDto> findAllCategoriesConvertedToDto();

    Category getCategoryById(Long categoryId);

    void deleteCategory(Category category);

    boolean existsByCategoryName(String categoryName);

    List<Category> findAllActiveCategories();

    void update(Category prod);

    void saveProductFromCategory(Product product);

    List<Category> findAllCategoriesWithOffers();
}
