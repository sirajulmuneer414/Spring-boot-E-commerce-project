package com.sirajul.lenscraft.Controller.Offer;

import ch.qos.logback.core.util.DefaultInvocationGate;
import com.sirajul.lenscraft.DTO.offer.OfferDto;
import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.Service.interfaces.ReferralOfferService;
import com.sirajul.lenscraft.entity.offer.OfferEmbeddable;
import com.sirajul.lenscraft.entity.offer.ReferralOffer;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.product.Product;
import com.sirajul.lenscraft.mapping.OfferDtoMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/offer")
public class AdminOfferController {

    @Autowired
    ProductService productService;

    @Autowired
    OfferDtoMapping offerDtoMapping;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ReferralOfferService referralOfferService;

    @GetMapping
    public String getAdminOffer(
            HttpSession session,
            Model model
    ){

        return "admin/offers/get-offers";
    }

    @GetMapping("/product/add/{id}")
    public String addAdminOffer(
            @PathVariable("id")Long productId,
            Model model
    ){

        Product product = productService.findProductById(productId);

        model.addAttribute("product",product);

        OfferDto offerDto = new OfferDto();

        model.addAttribute("offerDto",offerDto);



        return "admin/offers/add-product-offer";
    }
    @PostMapping("/product/add/{id}")
    public String PostAddAdminOffer(
            @PathVariable("id")Long productId,
            @ModelAttribute("offerDto")OfferDto offerDto,
            Model model
    ){
        Product product = productService.findProductById(productId);

        OfferEmbeddable offer = product.getOffer();

        System.out.println(offerDto.getStartDate());

        offer.setOfferDescription(offerDto.getOfferDescription());
        offer.setOfferPercentage(offerDto.getOfferPercentage());

        product.setDiscountedPrice(product.getPrice() - (product.getPrice() * offerDto.getOfferPercentage() / 100));

        offer.setStartDate(offerDto.getStartDate());

        offer.setEndDate(offerDto.getEndDate());

        offer.setMinimumQuantity(1);

        product.setOffer(offer);

        product.setHavingOffer(true);

        productService.save(product);

        return "redirect:/admin/products";
    }
    @PostMapping("/product/delete/{id}")
    public String deleteProductOffer(
            @PathVariable("id") Long productId
    ){

        Product product = productService.findProductById(productId);

        product.setOffer(new OfferEmbeddable());

        product.setDiscountedPrice(product.getPrice());

        product.setHavingOffer(false);

        productService.save(product);



        return "redirect:/admin/products";

    }

    @GetMapping("/product/edit/{id}")
    public String editProductOffer(
            @PathVariable("id") Long productId,
            Model model
    ){
        Product product = productService.findProductById(productId);

        OfferEmbeddable offer = product.getOffer();

        OfferDto offerDto = offerDtoMapping.offerToDto(offer);

        offerDto.setDiscountedPrice(product.getDiscountedPrice());

        model.addAttribute("productId",product.getProductId());
        model.addAttribute("productName",product.getProductName());
        model.addAttribute("offer",offerDto);


        return "admin/offers/edit-product-offer";
    }

    @PostMapping("/product/edit/{id}")
    public String editProductOfferPost(
            @PathVariable("id") Long productId,
            @ModelAttribute("offerDto") OfferDto dto,
            RedirectAttributes redirectAttributes
    ){
        Product product = productService.findProductById(productId);

        OfferEmbeddable offer = offerDtoMapping.editCheckingAndAdding(dto,product.getOffer());

        int discountedPrice = (product.getPrice() - (product.getPrice() * offer.getOfferPercentage() / 100));

        if(discountedPrice != product.getPrice() && discountedPrice != 0){
            product.setDiscountedPrice(discountedPrice);
        }

        productService.save(product);

        redirectAttributes.addFlashAttribute("editSuccess","The Offer of product with id "+ productId+" was successfully updated");

        return "redirect:/admin/products";
    }

    @GetMapping("/category/add/{id}")
    public String getAddCategoryOffer(
            @PathVariable("id") Long categoryId,
            Model model
    ){
        Category category = categoryService.findCategoryById(categoryId);

        OfferDto dto = new OfferDto();

        model.addAttribute("offerDto",dto);

        model.addAttribute("categoryId",categoryId);

        model.addAttribute("categoryName",category.getCategoryName());

        return "admin/offers/add-category-offer";
    }

    @PostMapping("/category/add/{id}")
    public String addCategoryOffer(
            @PathVariable("id") Long categoryId,
            @ModelAttribute("offerDto") OfferDto dto,
            RedirectAttributes redirectAttributes,
            Model model
    ){
        Category category = categoryService.findCategoryById(categoryId);

        OfferEmbeddable offer = category.getOffer();

        offer = offerDtoMapping.dtoToOffer(dto,offer);

        category.setHavingOffer(true);

        List<Product> products = category.getProducts();

        for(Product prod : products){
            if(!prod.isHavingOffer()){
                int discountedPrice = (prod.getPrice() - (prod.getPrice() * offer.getOfferPercentage() / 100));

                prod.setDiscountedPrice(discountedPrice);

            }
            productService.save(prod);
        }

        categoryService.addCategory(category);

        redirectAttributes.addFlashAttribute("OfferAdditionSuccess","The Category Offer of "+category.getCategoryName()+" added successfully.");

        return "redirect:/admin/category";
    }

    @PostMapping("/category/delete/{id}")
    public String deleteCategoryOffer(
            @PathVariable("id") Long categoryId,
            @RequestParam( name = "fromOffer",required = false,defaultValue = "false") String fromOffer
    ){

        Category category = categoryService.findCategoryById(categoryId);

        category.setOffer(new OfferEmbeddable());

        List<Product> products = category.getProducts();

        for(Product prod : products){
            if(!prod.isHavingOffer()){
                int discountedPrice = prod.getPrice();

                prod.setDiscountedPrice(discountedPrice);

            }
            productService.save(prod);
        }

        category.setHavingOffer(false);

        categoryService.addCategory(category);


        return "redirect:/admin/category";
    }

    @GetMapping("/category/edit/{id}")
    public String getEditCategoryOffer(
            @PathVariable("id") Long categoryId,
            @RequestParam( name = "fromOffer",required = false,defaultValue = "false") String fromOffer,
            Model model
    ){
        Category category = categoryService.findCategoryById(categoryId);

        OfferDto offerDto = offerDtoMapping.offerToDto(category.getOffer());

        model.addAttribute("offer",offerDto);

        model.addAttribute("categoryId",categoryId);

        model.addAttribute("categoryName",category.getCategoryName());

        model.addAttribute("fromOffer",fromOffer);

        return "admin/offers/edit-category-offer";
    }

    @PostMapping("/category/edit/{id}")
    public String editCategoryOffer(
            @PathVariable("id") Long categoryId,
            @ModelAttribute("offer") OfferDto dto
    ){
        Category category = categoryService.findCategoryById(categoryId);

        OfferEmbeddable offer = category.getOffer();

        offer = offerDtoMapping.editCheckingAndAdding(dto,offer);

        List<Product> products = category.getProducts();

        for(Product prod : products){
            if(!prod.isHavingOffer()){
                int discountedPrice = (prod.getPrice() - (prod.getPrice() * offer.getOfferPercentage() / 100));

                prod.setDiscountedPrice(discountedPrice);

            }
            productService.save(prod);
        }

        categoryService.addCategory(category);

        return "redirect:/admin/category";
    }

    @PostMapping("/referral/add")
    public String getAddReferralOffer(
            Model model,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("refer") ReferralOffer refer
    ){
        refer.setOfferId(1);


        referralOfferService.save(refer);

        return "redirect:/admin/customers";

    }

    @GetMapping("/referral/delete")
    public String deleteReferralOffer(

    ){
        referralOfferService.delete();

        return "redirect:/admin/customers";
    }

    @PostMapping("/referral/edit")
    public String getEditReferralOffer(
            Model model,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("refer") ReferralOffer refer
    ){


        referralOfferService.edit(refer);

        return "redirect:/admin/customers";

    }
}
