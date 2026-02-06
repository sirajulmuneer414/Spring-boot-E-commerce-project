package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.Product.ProductDto;
import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.Service.interfaces.RatingsService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.Service.interfaces.VariableService;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.entity.product.Variables;
import com.sirajul.lenscraft.entity.user.*;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import com.sirajul.lenscraft.mapping.ProductMapping;
import jakarta.persistence.GeneratedValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    CategoryService categoryService;

    @Autowired
    RatingsService ratingsService;

    @GetMapping()
    public String getShop(
            Model model,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "12", required = false) int pageSize

    ) {
        System.out.println("Entering ShopController.getShop()");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = null;

        UserInformation user = null;

        if (!(auth instanceof AnonymousAuthenticationToken)) {
            username = auth.getName();

            user = userService.findByEmailId(username);

            System.out.println(user.getPassword());
        }

        Page<Product> products;

        if (keyword != null) {
            products = productService.findAllProductsContainingActive(keyword, pageNo, pageSize);
        } else {
            products = productService.findAllProductsInPageableActive(pageNo, pageSize);
        }

        // OPTIMIZED: Batch fetch ratings data (fixes N+1 query issue)
        List<Long> productIds = products.stream()
                .map(Product::getProductId)
                .collect(java.util.stream.Collectors.toList());

        java.util.Map<Long, Double> ratingsMap = ratingsService.getAverageRatingsForProducts(productIds);
        java.util.Map<Long, Long> ratingsCountMap = ratingsService.getRatingsCountsForProducts(productIds);

        model.addAttribute("products", products);
        model.addAttribute("ratingsMap", ratingsMap);
        model.addAttribute("ratingsCountMap", ratingsCountMap);

        // Fetch active categories for sidebar
        List<Category> categories = categoryService.findAllActiveCategories();
        model.addAttribute("categories", categories);

        if (user != null) {
            model.addAttribute("username", user.getFirstName());
            model.addAttribute("userId", user.getUserId());
        }
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("totalPages", productService.totalPagesCount(pageSize));

        return "shop/shop";
    }

    @GetMapping("/category")
    public String getCategoryShop(
            Model model,
            @RequestParam(name = "categoryId") Long categoryId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "12", required = false) int pageSize

    ) {

        Category category = categoryService.findCategoryById(categoryId);

        Page<Product> products;

        List<Product> initialProducts = new ArrayList<>();
        if (keyword != null) {
            for (Product prod : productService.findAllProductsContaining(keyword)) {

                if (prod.getCategory() == category) {
                    if (prod.getActiveStatus() == ActiveStatus.ACTIVE) {
                        initialProducts.add(prod);
                    }
                }

            }

        } else {
            for (Product prod : productService.findAllProductByCategory(category)) {
                if (prod.getActiveStatus() == ActiveStatus.ACTIVE) {
                    initialProducts.add(prod);
                }

            }
        }
        Pageable pageRequest = PageRequest.of(pageNo - 1, pageSize);

        products = new PageImpl<>(
                initialProducts.subList((int) pageRequest.getOffset(),
                        (int) Math.min((pageRequest.getOffset() + pageRequest.getPageSize()), initialProducts.size())),
                pageRequest, initialProducts.size());

        // OPTIMIZED: Batch fetch ratings data (fixes N+1 query issue)
        List<Long> productIds = products.stream()
                .map(Product::getProductId)
                .collect(java.util.stream.Collectors.toList());

        java.util.Map<Long, Double> ratingsMap = ratingsService.getAverageRatingsForProducts(productIds);
        java.util.Map<Long, Long> ratingsCountMap = ratingsService.getRatingsCountsForProducts(productIds);

        model.addAttribute("products", products);
        model.addAttribute("ratingsMap", ratingsMap);
        model.addAttribute("ratingsCountMap", ratingsCountMap);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("totalPages", initialProducts.size() / pageSize);

        return "shop/category-shop";
    }

    @GetMapping("/productView/{productId}/{variableId}")
    public String getProductView(@PathVariable("productId") Long productId,
            @PathVariable("variableId") Long variableId,
            Model model) {

        Product product = productService.findProductById(productId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();

        log.info(username);

        model.addAttribute("username", username);

        Variables targetVariable = variableService.findVariableById(variableId);

        model.addAttribute("targetVariable", targetVariable);

        List<Variables> variables = product.getVariables();

        model.addAttribute("variables", variables);

        boolean wishlisted = false;

        boolean carted = false;

        if (!(auth instanceof AnonymousAuthenticationToken)) {

            UserInformation user = userService.findByEmailId(username);

            model.addAttribute("userId", user.getUserId());

            Wishlist wishlist = user.getWishlist();

            Cart cart = user.getCart();

            if (wishlist.getProducts().contains(product)) {
                wishlisted = true;
            }
            if (cart != null) {

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

        model.addAttribute("product", product);

        // Add ratings data
        Double averageRating = ratingsService.getAverageRating(productId);
        Long ratingsCount = ratingsService.getRatingsCount(productId);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("ratingsCount", ratingsCount);

        return "shop/product-view";
    }

    @GetMapping("/variableChange/{id}")
    public String getVariableChange(@PathVariable("id") Long variableId) {
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
