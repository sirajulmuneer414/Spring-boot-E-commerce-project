package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.shop.CategoryHomeDto;
import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.entity.user.UserInformation;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class ControllerHome {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String getShop(Model model){

        List<CategoryHomeDto> categories = categoryService.findAllCategoriesConvertedToDto();

        List<Product> productList =  productService.findAllProducts();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();
        UUID userId = null;
if(!(auth instanceof AnonymousAuthenticationToken)) {
    UserInformation user = userService.findByEmailId(username);
    userId = user.getUserId();
}
        System.out.println(username);

        model.addAttribute("categories",categories);
        model.addAttribute("productList",productList);
        model.addAttribute("username",username);
        model.addAttribute("userId",userId);


        return"home";
    }

}
