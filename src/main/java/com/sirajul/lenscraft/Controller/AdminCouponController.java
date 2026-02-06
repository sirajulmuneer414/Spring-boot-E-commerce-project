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
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    @Autowired
    CouponService couponService;

    @GetMapping
    public String getAdminSideCoupon(
            Model model) {
        model.addAttribute("coupons", couponService.findAllCoupons());
        model.addAttribute("dto", new CouponDto());

        return "admin/coupon/get-coupons";

    }

    @GetMapping("/add")
    public String getAddCoupon(Model model) {
        model.addAttribute("dto", new CouponDto());
        return "admin/coupon/add-coupon";
    }

    @PostMapping("/add")
    public String postAddAdminSideCoupon(
            @ModelAttribute("dto") CouponDto dto,
            RedirectAttributes redirectAttributes) {
        if (dto.getCouponCode() == null || dto.getCouponCode().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("CouponExists", "Coupon code cannot be empty");
            return "redirect:/admin/coupons/add";
        }

        if (dto.getCouponName() == null || dto.getCouponName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("CouponExists", "Coupon name cannot be empty");
            return "redirect:/admin/coupons/add";
        }

        if (dto.getDiscountPercentage() <= 0 || dto.getDiscountPercentage() > 100) {
            redirectAttributes.addFlashAttribute("CouponExists", "Discount percentage must be between 1 and 100");
            return "redirect:/admin/coupons/add";
        }

        if (dto.getMinimumAmount() < 0) {
            redirectAttributes.addFlashAttribute("CouponExists", "Minimum amount cannot be negative");
            return "redirect:/admin/coupons/add";
        }

        if (couponService.existsByCouponCode(dto.getCouponCode())) {
            redirectAttributes.addFlashAttribute("CouponExists", "Coupon already exists");
            return "redirect:/admin/coupons/add";
        }

        if (dto.getStartDate().isBefore(java.time.LocalDate.now())) {
            redirectAttributes.addFlashAttribute("CouponExists", "Start date cannot be in the past");
            return "redirect:/admin/coupons/add";
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            redirectAttributes.addFlashAttribute("CouponExists", "End date must be after start date");
            return "redirect:/admin/coupons/add";
        }

        couponService.addCoupon(dto);
        redirectAttributes.addFlashAttribute("CouponExists", "Coupon added successfully");

        return "redirect:/admin/coupons";
    }

    @PostMapping("/delete/{id}")
    public String deleteCoupon(
            @PathVariable("id") UUID couponId) {

        couponService.deleteById(couponId);
        return "redirect:/admin/coupons";
    }

    @PostMapping("/edit")
    public String editCoupon(
            @RequestParam("couponId") UUID couponId,
            @ModelAttribute("dto") CouponDto dto) {
        couponService.updateCoupon(dto, couponId);

        return "redirect:/admin/coupons";
    }

}
