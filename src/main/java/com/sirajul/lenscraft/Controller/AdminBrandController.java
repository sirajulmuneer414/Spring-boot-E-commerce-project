package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.ToPassBoolean;
import com.sirajul.lenscraft.Service.interfaces.BrandService;
import com.sirajul.lenscraft.entity.product.Brand;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.enums.BrandStatus;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/brand")
public class AdminBrandController {

    @Autowired
    BrandService brandService;

    @GetMapping()
    public String getBrands(Model model) {

        List<Brand> brands = brandService.findAllBrands();

        model.addAttribute("brands", brands);

        return "admin/get-brands";
    }

    @GetMapping("/add")
    public String getAddBrands() {
        return "admin/add-brand";
    }

    @PostMapping("/add")
    public String addBrand(@RequestParam("brandName") String brandName) {
        brandService.addBrand(brandName);
        return "redirect:/admin/brand";
    }

    @GetMapping("/editCheck")
    @ResponseBody
    public ResponseEntity<ToPassBoolean> editBrand(@RequestParam("brandName") String brandName,
            @RequestParam("brandId") Integer brandId) {
        ToPassBoolean checker = new ToPassBoolean();
        Brand brand = brandService.findBrandById(brandId);

        if (brand.getBrandName().toLowerCase().matches(brandName.toLowerCase())) {
            brandService.addBrand(brandName);
            checker.setCheck(true);
            return new ResponseEntity<>(checker, HttpStatus.OK);
        } else if (brandService.existByBrandName(brandName)) {
            checker.setCheck(false);
            return new ResponseEntity<>(checker, HttpStatus.OK);

        }

        brand.setBrandName(brandName);
        brandService.update(brand);
        checker.setCheck(true);
        return new ResponseEntity<>(checker, HttpStatus.OK);
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Integer brandId, Model model) {

        Brand brand = brandService.findBrandById(brandId);

        model.addAttribute("brand", brand);

        return "admin/edit-brand";
    }

    @PostMapping("/edit/{id}")
    public String updateBrand(@PathVariable("id") Integer brandId,
            @ModelAttribute Brand brandFromForm) {
        Brand brand = brandService.findBrandById(brandId);

        if (brandFromForm.getBrandName() != null && !brandFromForm.getBrandName().isEmpty()) {
            brand.setBrandName(brandFromForm.getBrandName());
        }

        brandService.update(brand);
        return "redirect:/admin/brand";
    }

    @GetMapping("/block/{id}")
    public String blockBrand(
            @PathVariable("id") Integer brandId) {

        Brand brand = brandService.findBrandById(brandId);

        brand.setBrandStatus(BrandStatus.BLOCKED);

        List<Product> products = brand.getProducts();

        for (Product product : products) {

            product.setActiveStatus(ActiveStatus.BLOCKED);

            brandService.saveProductFromBrand(product);
        }

        brandService.update(brand);

        return "redirect:/admin/brand";

    }

    @GetMapping("/unblock/{id}")
    public String unblockBrand(
            @PathVariable("id") Integer brandId) {

        Brand brand = brandService.findBrandById(brandId);

        brand.setBrandStatus(BrandStatus.ACTIVE);
        List<Product> products = brand.getProducts();

        for (Product product : products) {

            product.setActiveStatus(ActiveStatus.ACTIVE);

            brandService.saveProductFromBrand(product);
        }

        brandService.update(brand);

        return "redirect:/admin/brand";

    }

    @PostMapping("/delete/{id}")
    public String deleteBrand(
            @PathVariable("id") Integer brandId,
            RedirectAttributes redirectAttributes) {
        Brand brand = brandService.findBrandById(brandId);

        if (brand.getProducts() != null && !brand.getProducts().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete brand with existing products");
            return "redirect:/admin/brand";
        }

        brandService.deleteById(brandId);
        return "redirect:/admin/brand";
    }

}
