package com.sirajul.lenscraft.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @GetMapping("/")
    public String getProductPage(Model model){


    return "admin/getProducts";
    }

}
