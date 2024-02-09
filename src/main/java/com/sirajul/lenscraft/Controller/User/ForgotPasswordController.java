package com.sirajul.lenscraft.Controller.User;

import com.sirajul.lenscraft.DTO.password.PasswordChangerDto;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.utils.OtpUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    @Autowired
    OtpUtil otpUtil;

    @Autowired
    PasswordChangerDto passwordChangerDto;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @GetMapping()
    public String getForgotPassword(


    ){
        return "user/forgotPassword/forgot-password";
    }

    @GetMapping("/email")
    public String getEmailForPasswordChange(
            Model model,
            @RequestParam("emailId") String emailId,
            HttpSession session
    ){
        passwordChangerDto.setEmailId(emailId);
        passwordChangerDto.setOtp(otpUtil.generateOtp());
        passwordChangerDto.setOtpGeneratedTime(LocalDateTime.now());

        otpUtil.sendOtpEmailForPasswordChanging(emailId,passwordChangerDto.getOtp());

        session.setAttribute("otp",passwordChangerDto);


        return "user/forgotPassword/forgot-password-otp";
    }

    @PostMapping("/email")
    public String getOtpFromFront(
            Model model,
            @RequestParam("otp") String otp,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ){
        PasswordChangerDto dto = (PasswordChangerDto) session.getAttribute("otp");

        if(otpUtil.isOtpValidForChangingPassword(dto,otp)){

            session.setAttribute("otp",dto);

            return "user/forgotPassword/new_password_forgot";

        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        redirectAttributes.addFlashAttribute("changePasswordFailed","OOps ! The OTP you have entered is WRONGGG!");
        if(! (auth instanceof AnonymousAuthenticationToken)){
            return "redirect:/user/profile";
        }

        return "redirect:/login";
    }
    @GetMapping("/change")
    public String changePasswordForgot(
            RedirectAttributes redirectAttributes,
            HttpSession session,
            @RequestParam("password") String password
    ){
        PasswordChangerDto dto = (PasswordChangerDto) session.getAttribute("otp");

        System.out.println(dto.getEmailId());
        System.out.println(password);

        UserInformation user = userService.findByEmailId(dto.getEmailId());

        user.setPassword(passwordEncoder.encode(password));

        userService.save(user);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        redirectAttributes.addFlashAttribute("changePasswordSuccess","Password changed successfully");
        if(! (auth instanceof AnonymousAuthenticationToken)){
            return "redirect:/user/profile";
        }

        return "redirect:/login";
    }
}
