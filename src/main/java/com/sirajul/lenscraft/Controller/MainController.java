package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.SignupDto;
import com.sirajul.lenscraft.Service.interfaces.ReferralOfferService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.exception.InvalidOtpException;

import com.sirajul.lenscraft.utils.OtpUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.sirajul.lenscraft.Service.interfaces.CategoryService;
import com.sirajul.lenscraft.Service.interfaces.ProductService;
import com.sirajul.lenscraft.entity.product.Category;
import com.sirajul.lenscraft.entity.user.UserInformation;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
public class MainController {

    @Autowired
    UserService userService;

    @Autowired
    ReferralOfferService referralOfferService;

    @Autowired
    OtpUtil otpUtil;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @GetMapping("/register")
    public String getRegister(Model model, RedirectAttributes redirectAttributes, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            redirectAttributes.addFlashAttribute("authMessage", "Please log out to access the registration page.");
            if (AuthorityUtils.authorityListToSet(auth.getAuthorities()).contains(Role.ADMIN.name())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/";
        }

        // Prevent back-button from showing cached page after login
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        boolean referIsThere = referralOfferService.isOfferAlreadyEstablished();
        SignupDto signupDto = new SignupDto();
        model.addAttribute("user", signupDto);
        model.addAttribute("referIsThere", referIsThere);

        return "signup";
    }

    @org.springframework.beans.factory.annotation.Value("${app.admin-code}")
    private String adminCodeSecret;

    @Autowired
    com.sirajul.lenscraft.Service.interfaces.CloudinaryService cloudinaryService;

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") SignupDto signupDto,
            HttpSession httpSession,
            RedirectAttributes redirectAttributes,
            @RequestParam(name = "adminPassword", required = false) String adminPassword,
            @RequestParam(name = "refer", required = false) String referralCode,
            @RequestParam(name = "image", required = false) MultipartFile file) throws IOException {

        boolean userExists = userService.userExistsByEmail(signupDto.getEmailId());

        log.info(adminPassword);

        if (userExists) {
            String errorMessage = "The Email Id you have entered already exists";
            redirectAttributes.addFlashAttribute("EmailError", errorMessage);

            return "redirect:/register";

        }
        if (file != null && !file.isEmpty()) {
            log.info("inside profile picture uploading");
            String folder = "lenscraft/profileImages/" + signupDto.getEmailId();
            String url = cloudinaryService.uploadFile(file, folder);
            signupDto.setProfilePic(url);
        }

        String otp = otpUtil.generateOtp();
        System.out.println("This here is the OTP : " + otp);
        signupDto.setOtp(otp);
        signupDto.setOtpGeneratedTime(LocalDateTime.now());

        otpUtil.sendOtpEmail(signupDto.getEmailId(), otp);

        if (adminPassword != null && adminPassword.matches(adminCodeSecret)) {
            signupDto.setRole(Role.ADMIN.name());
        }
        httpSession.setAttribute("Non-verifiedUser", signupDto);

        log.info(signupDto.toString());
        return "redirect:/verification";
    }

    @GetMapping("/verification")
    public String getVerification() {
        return "verification";
    }

    @PostMapping("/verification")
    public String verification(@RequestParam("otp") String otp, HttpSession httpSession,
            Model model) {
        log.info("inside verification posting");
        SignupDto user = (SignupDto) httpSession.getAttribute("Non-verifiedUser");
        System.out.println(user);
        log.info("got user");

        try {
            if (userService.verifyOtpAndSave(user, otp)) {
                log.info("went inside try");
                return "redirect:/login?message=success";
            }
        } catch (InvalidOtpException e) {
            model.addAttribute("otpError", e.getMessage() + ", Please Try Again");
        }
        return "verification";
    }

    @GetMapping("/register/cancel")
    public String cancelRegistration(
            HttpSession session) {
        session.removeAttribute("Non-verifiedUser");

        return "redirect:/register";

    }

    @GetMapping("/resend-otp")
    public String resendOtp(HttpSession httpSession, RedirectAttributes redirectAttributes) {
        SignupDto signupDto = (SignupDto) httpSession.getAttribute("Non-verifiedUser");

        String otp = otpUtil.generateOtp();

        otpUtil.resendOtpEmail(signupDto.getEmailId(), otp);

        signupDto.setOtp(otp);

        redirectAttributes.addFlashAttribute("resent", "New OTP has been send to your mail");
        return "redirect:/verification";
    }

    @GetMapping("/login")
    public String getLogin(RedirectAttributes redirectAttributes, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            redirectAttributes.addFlashAttribute("authMessage", "Please log out to access the login page.");
            if (AuthorityUtils.authorityListToSet(auth.getAuthorities()).contains(Role.ADMIN.name())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/";
        }

        // Prevent back-button from showing cached page after login
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "login";
    }

    @GetMapping("/")
    public String getShop(Model model) {
        System.out.println("Entering root mapping /");
        log.info("Entering root mapping /");

        List<Category> categories = categoryService.findAllActiveCategories();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();
        UUID userId = null;
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserInformation user = userService.findByEmailId(username);
            if (user != null) {
                userId = user.getUserId();
                model.addAttribute("username", user.getFirstName()); // Use firstName for profile icon
            }
        }

        System.out.println("Username: " + username);
        model.addAttribute("categories", categories);
        // username is now valid only if logged in
        model.addAttribute("userId", userId);

        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/order")
    public String redirectToUserOrders() {
        return "redirect:/user/orders";
    }
}
