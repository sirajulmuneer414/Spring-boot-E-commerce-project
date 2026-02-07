package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.DTO.shop.CategoryHomeDto;
import com.sirajul.lenscraft.Repository.CategoryRepository;
import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.enums.CategoryStatus;
import com.sirajul.lenscraft.mapping.CategoryDtoMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductService productService;

    @Autowired
    CategoryDtoMapping categoryDtoMapping;

    @Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).get();
    }

    @Override
    public void addCategory(Category category) {

        categoryRepository.save(category);

    }

    @Override
    public List<CategoryHomeDto> findAllCategoriesConvertedToDto() {
        List<CategoryHomeDto> categoriesDto = new ArrayList<>();

        for (Category category : categoryRepository.findAll()) {
            CategoryHomeDto dto = new CategoryHomeDto();

            dto.setCategoryId(category.getCategoryId());
            dto.setCategoryName(category.getCategoryName());
            dto.setProducts(new ArrayList<>());
            if (category.getProducts().size() > 4) {
                for (int i = 0; i < 4; i++) {
                    dto.getProducts().add(category.getProducts().get(i));
                }
            } else {
                dto.setProducts(category.getProducts());
            }

            categoriesDto.add(dto);

        }

        return categoriesDto;
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).get();
    }

    @Override
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {
        return categoryRepository.existsByCategoryNameIgnoreCase(categoryName);
    }

    @Override
    public List<Category> findAllActiveCategories() {
        return categoryRepository.findAllByCategoryStatus(CategoryStatus.ACTIVE);
    }

    @Override
    public void update(Category prod) {
        categoryRepository.save(prod);
    }

    @Override
    public void saveProductFromCategory(Product product) {
        productService.save(product);
    }

    @Override
    public List<Category> findAllCategoriesWithOffers() {
        return categoryRepository.findAllByIsHavingOfferTrue();
    }
}
