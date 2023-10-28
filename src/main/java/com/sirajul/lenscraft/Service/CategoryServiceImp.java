package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.Repository.CategoryRepository;
import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.entity.product.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService
{

    @Autowired
    CategoryRepository categoryRepository;

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
}
