package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.Product.ProductDto;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.Service.interfaces.VariableService;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.entity.user.Cart;
import com.sirajul.lenscraft.entity.user.CartedItems;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.Wishlist;
import com.sirajul.lenscraft.mapping.ProductMapping;
import jakarta.persistence.GeneratedValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shop")
@Slf4j
public class ShopController {

    @Autowired
    ProductService productService;

    @Autowired
    VariableService variableService;

    @Autowired
    ProductMapping productMapping;

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String getShop(
            Model model,
            @RequestParam(name="keyword",required = false) String keyword,
            @RequestParam(name="pageNo",defaultValue = "1",required = false) int pageNo,
            @RequestParam(name="pageSize",defaultValue = "10",required = false) int pageSize

    )
    {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = null;

        UserInformation user = null;

    if(!(auth instanceof AnonymousAuthenticationToken)) {
        username = auth.getName();

        user = userService.findByEmailId(username);

        System.out.println(user.getPassword());
    }

        Page<Product> products;

        if(keyword !=null){
            products = productService.findAllProductsContaining(keyword,pageNo,pageSize);
        }
        else {
            products = productService.findAllProductsInPageable(pageNo,pageSize);
        }




        model.addAttribute("products",products);
        if(user != null) {
            model.addAttribute("username", user.getFirstName());
            model.addAttribute("userId", user.getUserId());
        }
        model.addAttribute("pageNo",pageNo);
        model.addAttribute("totalPages",productService.totalPagesCount(pageSize));


        return "shop/shop";
    }

    @GetMapping("/productView/{productId}/{variableId}")
    public String getProductView(@PathVariable("productId")Long productId,
                                 @PathVariable("variableId")Long variableId,
                                 Model model){

        Product product = productService.findProductById(productId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();

        log.info(username);

        model.addAttribute("username",username);

        Variables targetVariable = variableService.findVariableById(variableId);

        model.addAttribute("targetVariable",targetVariable);

        List<Variables> variables = product.getVariables();

        model.addAttribute("variables",variables);

        boolean wishlisted = false;

        boolean carted = false;

        if(!(auth instanceof AnonymousAuthenticationToken)) {

            UserInformation user = userService.findByEmailId(username);

            model.addAttribute("userId",user.getUserId());

            Wishlist wishlist = user.getWishlist();

            Cart cart = user.getCart();

            if(wishlist.getProducts().contains(product)){
                wishlisted = true;
            }
            if(cart != null) {

                for (CartedItems item : cart.getCartedItems()) {

                    if (item.getProduct() == product && item.getVariable() == targetVariable) {
                        carted = true;

                        model.addAttribute("cartedItemId", item.getCartedItemId());
                    }

                }
            }
        }
        model.addAttribute("wishlisted", wishlisted);

        model.addAttribute("carted", carted);

        model.addAttribute("product",product);

        return "shop/product-view";
    }

    @GetMapping("/variableChange/{id}")
    public String getVariableChange(@PathVariable("id")Long variableId){
        Variables variable = variableService.findVariableById(variableId);

        Product product = variable.getProduct();

        System.out.println(product.getProductId());
        System.out.println(product.getProductName());
        System.out.println(product.getPrice());
        System.out.println(product.getDescription());
        System.out.println(product.getModelNo());
        System.out.println(product.getCategory().getCategoryName());
        System.out.println(product.getBrand().getBrandName());
        System.out.println(product.getFrameSize().name());
        System.out.println(product.getStockStatus().name());

        return "shop/product-view";

    }




}
