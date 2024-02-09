package com.sirajul.lenscraft.Controller.User;

import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.user.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user/category")
public class UserCategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @GetMapping("/shop_by_category")
    public String getShopByCategory(
            Model model,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(name = "pageNo",defaultValue = "1", required = false)int pageNo

    ){
        Category category =  categoryService.findCategoryById(categoryId);
        List<Product> products = productService.findAllProductByCategory(category);

        Pageable pageable = PageRequest.of(pageNo-1,12);


        Page<Product> orderPage = new PageImpl<>(products.subList((int) pageable.getOffset(), (int) Math.min((pageable.getOffset() + pageable.getPageSize()), products.size())), pageable, products.size());

        model.addAttribute("products",orderPage);

        return "";

    }
}
