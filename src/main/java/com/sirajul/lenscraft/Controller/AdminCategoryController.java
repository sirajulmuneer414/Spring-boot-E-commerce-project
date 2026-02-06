package com.sirajul.lenscraft.Controller;

import ch.qos.logback.core.util.DefaultInvocationGate;
import com.sirajul.lenscraft.DTO.ToPassBoolean;
import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.Service.interfaces.CloudinaryService;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.enums.CategoryStatus;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping()
    public String getCategories(Model model) {

        List<Category> categories = categoryService.findAllCategories();

        model.addAttribute("categories", categories);

        return "admin/categoriesPage";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Long categoryId, Model model) {

        Category category = categoryService.findCategoryById(categoryId);

        model.addAttribute("category", category);

        return "admin/editCategory";
    }

    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<ToPassBoolean> addCategoryCheck(@RequestParam("brandName") String brandName) {
        ToPassBoolean checker = new ToPassBoolean();
        if (categoryService.existsByCategoryName(brandName)) {
            checker.setCheck(false);
            return new ResponseEntity<>(checker, HttpStatus.IM_USED);
        }
        checker.setCheck(true);
        return new ResponseEntity<>(checker, HttpStatus.ACCEPTED);
    }

    @PostMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Long categoryId,
            @ModelAttribute Category categoryFromEdit,
            Model model) {

        Category category = categoryService.findCategoryById(categoryId);

        if (categoryFromEdit.getCategoryName() != "" && categoryFromEdit.getCategoryName() != category.getCategoryName()
                && categoryService.existsByCategoryName(categoryFromEdit.getCategoryName())) {
            category.setCategoryName(categoryFromEdit.getCategoryName());
        }

        if (categoryFromEdit.getCategoryDescription() != ""
                && categoryFromEdit.getCategoryDescription() != category.getCategoryDescription()) {
            category.setCategoryDescription(categoryFromEdit.getCategoryDescription());
        }
        categoryService.addCategory(category);

        return "redirect:/admin/category";
    }

    @GetMapping("/add")
    public String getAddCategory(Model model) {

        Category category = new Category();
        model.addAttribute("categoryToAdd", category);

        return "admin/addCategory";

    }

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/add")
    public String addCategory(
            @ModelAttribute("categoryToAdd") Category category,
            @RequestParam(name = "image", required = false) MultipartFile image) {

        category.setCategoryStatus(CategoryStatus.ACTIVE);
        if (image != null && !image.isEmpty()) {
            String folder = "lenscraft/categoryImages";
            // Note: Category ID might not be available yet if it's auto-generated, so using
            // a specific folder or name might be tricky.
            // Using a general folder for categories.
            String url = cloudinaryService.uploadFile(image, folder);
            category.setCategoryImage(url);
        }

        categoryService.addCategory(category);

        return "redirect:/admin/category";
    }

    @GetMapping("/block/{id}")
    public String blockCategory(@PathVariable("id") Long categoryId) {

        Category category = categoryService.getCategoryById(categoryId);

        category.setCategoryStatus(CategoryStatus.BLOCKED);

        List<Product> products = category.getProducts();

        for (Product product : products) {

            product.setActiveStatus(ActiveStatus.BLOCKED);

            categoryService.saveProductFromCategory(product);
        }

        categoryService.addCategory(category);

        return "redirect:/admin/category";

    }

    @GetMapping("/unblock/{id}")
    public String unBlockCategory(@PathVariable("id") Long categoryId) {

        Category category = categoryService.getCategoryById(categoryId);

        category.setCategoryStatus(CategoryStatus.ACTIVE);

        List<Product> products = category.getProducts();

        for (Product product : products) {

            product.setActiveStatus(ActiveStatus.ACTIVE);

            categoryService.saveProductFromCategory(product);
        }

        categoryService.addCategory(category);

        return "redirect:/admin/category";

    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long categoryId,
            RedirectAttributes redirectAttributes) {

        Category category = categoryService.findCategoryById(categoryId);

        if (category.getProducts().isEmpty()) {
            categoryService.deleteCategory(category);
            return "redirect:/admin/category";
        }
        redirectAttributes.addFlashAttribute("error", "The category already in use");
        return "redirect:/admin/category";

    }

}
