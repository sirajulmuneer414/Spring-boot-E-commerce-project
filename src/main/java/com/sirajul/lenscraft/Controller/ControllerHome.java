package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ControllerHome {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/")
    public String getShop(Model model){

        List<Category> categories = categoryService.findAllCategories();

        model.addAttribute(categories);


        return"home";
    }

}
