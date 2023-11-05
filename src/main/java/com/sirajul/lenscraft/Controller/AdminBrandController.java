package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.Service.interfaces.BrandService;
import com.sirajul.lenscraft.entity.product.Brand;
import com.sirajul.lenscraft.entity.product.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/brand")
public class AdminBrandController {

    @Autowired
    BrandService brandService;
    @GetMapping("/")
    public String getBrands(Model model){

        List<Brand> brands = brandService.findAllBrands();
        model.addAttribute("brands",brands);

        return "admin/get-brands";
    }

    @GetMapping("/add")
    public String getAddBrands(){
        return "admin/add-brand";
    }

    @PostMapping("/add")
    public String addBrand(@RequestParam("brandName") String brandName){
        brandService.addBrand(brandName);
        return "redirect:/admin/brand/";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Integer brandId, Model model){

        Brand brand = brandService.findBrandById(brandId);

        model.addAttribute("brandToEdit",brand);

        return "admin/edit-brand";
    }


}
