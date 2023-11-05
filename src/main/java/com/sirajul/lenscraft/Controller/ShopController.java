package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.Product.ProductDto;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.Service.interfaces.VariableService;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.mapping.ProductMapping;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    ProductService productService;

    @Autowired
    VariableService variableService;

    @Autowired
    ProductMapping productMapping;

    @GetMapping("/")
    public String getShop(Model model){



        List<Product> products = productService.findAllProducts();

        List<ProductDto> productDtos = new ArrayList<>();

        for(Product product : products){

            productDtos.add(productMapping.ProductToDto(product));

        }


        model.addAttribute("products",productDtos);


        return "shop/shop";
    }

    @GetMapping("/productView/{id}")
    public String getProductView(@PathVariable("id")Long productId,Model model){

        Product product = productService.findProductById(productId);

        model.addAttribute(product);

        List<Variables> variables = product.getVariables();

        model.addAttribute(variables);

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
