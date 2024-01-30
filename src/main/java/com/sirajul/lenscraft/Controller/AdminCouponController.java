package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.Product.CouponDto;
import com.sirajul.lenscraft.Service.interfaces.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/coupon")
public class AdminCouponController {

    @Autowired
    CouponService couponService;

    @GetMapping
    public String getAdminSideCoupon(
            Model model
    ){
        model.addAttribute("coupons",couponService.findAllCoupons());
        model.addAttribute("dto",new CouponDto());

        return "admin/coupon/get-coupons";

    }

    @PostMapping("/add")
    public String postAddAdminSideCoupon(
            @ModelAttribute("dto") CouponDto dto,
            RedirectAttributes redirectAttributes
    ){
        if(couponService.existsByCouponCode(dto.getCouponCode())){
            redirectAttributes.addFlashAttribute("CouponExists","Coupon already exists");
            return "redirect:/admin/coupon";
        }

        couponService.addCoupon(dto);
        redirectAttributes.addFlashAttribute("CouponExists","Coupon added successfully");

        return "redirect:/admin/coupon";
    }

    @PostMapping("/delete")
    public String deleteCoupon(
            @RequestParam("couponId")UUID couponId
            ){

        couponService.deleteById(couponId);
        return "redirect:/admin/coupon";
    }

    @PostMapping("/edit")
    public String editCoupon(
            @RequestParam("couponId")UUID couponId,
            @ModelAttribute("dto") CouponDto dto
    ){
        couponService.updateCoupon(dto,couponId);

        return "redirect:/admin/coupon";
    }

}
