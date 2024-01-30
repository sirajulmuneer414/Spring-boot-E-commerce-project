package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.enums.CategoryStatus;
import com.sirajul.lenscraft.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping()
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
    public String addCategory(
            @ModelAttribute("categoryToAdd") Category category,
            @RequestParam("image")MultipartFile image
            ) throws IOException {

        category.setCategoryStatus(CategoryStatus.ACTIVE);
        if (!image.isEmpty()) {
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());


            String upload = "lenscraft/src/main/resources/static/categoryImage";

            FileUploadUtil.saveFile(upload, fileName, image);

            category.setCategoryImage(fileName);

        }

        categoryService.addCategory(category);


        return "redirect:/admin/category/";
    }

    @GetMapping("/block/{id}")
    public String blockCategory(@PathVariable("id")Long categoryId){

        Category category = categoryService.getCategoryById(categoryId);

        category.setCategoryStatus(CategoryStatus.BLOCKED);

        categoryService.addCategory(category);

        return "redirect:/admin/category/";

    }
    @GetMapping("/unBlock/{id}")
    public String unBlockCategory(@PathVariable("id")Long categoryId){

        Category category = categoryService.getCategoryById(categoryId);

        category.setCategoryStatus(CategoryStatus.ACTIVE);

        categoryService.addCategory(category);

        return "redirect:/admin/category/";

    }


}
