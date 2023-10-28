package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.entity.product.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/")
    public String getCategories(Model model){

        List<Category> categories = categoryService.findAllCategories();

        model.addAttribute("categories",categories);

        return "admin/categoriesPage";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Long categoryId, Model model){

        Category category = categoryService.findCategoryById(categoryId);

        model.addAttribute("categoryToEdit",category);

        return "admin/editCategory";
    }

    @GetMapping("/add")
    public String getAddCategory(Model model){

        Category category = new Category();
        model.addAttribute("categoryToAdd",category);

        return "admin/addCategory";

    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute("categoryToAdd") Category category){

        categoryService.addCategory(category);


        return "redirect:/admin/category";
    }


}
