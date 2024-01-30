package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.SignupDto;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.exception.InvalidOtpException;
import com.sirajul.lenscraft.utils.FileUploadUtil;
import com.sirajul.lenscraft.utils.OtpUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@Slf4j
public class MainController {

    @Autowired
    UserService userService;

    @Autowired
    OtpUtil otpUtil;
     @GetMapping("/register")
        public String getRegister(Model model){

         SignupDto signupDto = new SignupDto();
         model.addAttribute("user",signupDto);

         return "signup";
        }

     @PostMapping("/register")
        public String registerUser(@ModelAttribute("user") SignupDto signupDto,
                                   HttpSession httpSession,
                                   RedirectAttributes redirectAttributes,
                                   @RequestParam(name="adminPassword",required = false)String adminPassword,
                                   @RequestParam(name="refer",required = false)String referralCode,
                                   @RequestParam("image") MultipartFile file
                                   ) throws IOException {

        boolean userExists = userService.userExistsByEmail(signupDto.getEmailId());

        log.info(adminPassword);

        if(userExists){
            String errorMessage = "The Email Id you have entered already exists";
            redirectAttributes.addFlashAttribute("EmailError",errorMessage);

            return "redirect:/register";

        }
         if (!file.isEmpty()) {
             String fileName = StringUtils.cleanPath(file.getOriginalFilename());
             log.info("inside profile picture uploading");

             String upload = "lenscraft/src/main/resources/static/profilePic/"+signupDto.getEmailId();

             FileUploadUtil.saveFile(upload, fileName, file);
             signupDto.setProfilePic(fileName);


         }


        String otp = otpUtil.generateOtp();
        signupDto.setOtp(otp);
        signupDto.setOtpGeneratedTime(LocalDateTime.now());

        otpUtil.sendOtpEmail(signupDto.getEmailId(),otp);

        if(adminPassword.matches("LNSCRFT2001F80")){
            signupDto.setRole(Role.ADMIN.name());
        }
        httpSession.setAttribute("Non-verifiedUser",signupDto);

            log.info(signupDto.toString());
         return "redirect:/verification";
     }

     @GetMapping("/verification")
    public String getVerification(){
            return "verification";
     }

     @PostMapping("/verification")
    public  String verification(@RequestParam("otp") String otp, HttpSession httpSession,
                               Model model){
         log.info("inside verification posting");
            SignupDto user = (SignupDto) httpSession.getAttribute("Non-verifiedUser");
         System.out.println(user);
         log.info("got user");

        try {
            if (userService.verifyOtpAndSave(user, otp)) {
                log.info("went inside try");
                return "redirect:/login?message=success";
            }
        }catch (InvalidOtpException e){
            model.addAttribute("otpError",e.getMessage()+", Please Try Again");
        }
            return "verification";
     }

     @GetMapping("/register/cancel")
     public String cancelRegistration(
             HttpSession session
     ){
         session.removeAttribute("Non-verifiedUser");

         return "redirect:/register";

     }

     @GetMapping("/resend-otp")
    public String resendOtp(HttpSession httpSession, RedirectAttributes redirectAttributes
     ){
         SignupDto signupDto = (SignupDto) httpSession.getAttribute("Non-verifiedUser");

         String otp = otpUtil.generateOtp();

         otpUtil.resendOtpEmail(signupDto.getEmailId(), otp);

         signupDto.setOtp(otp);

         redirectAttributes.addFlashAttribute("resent","New OTP has been send to your mail");
         return "redirect:/verification";
     }
     @GetMapping("/login")
     public String getLogin(){
         return "login";
     }

//     @GetMapping("/signup")
//    public String loginProcessing(){
//         Authentication securityAuthentication = SecurityContextHolder.getContext().getAuthentication();
//         Set<String> roles = AuthorityUtils.authorityListToSet(securityAuthentication.getAuthorities());
//         if(!(securityAuthentication instanceof AnonymousAuthenticationToken)) {
//             if (roles.contains(Role.ADMIN.name())) {
//                 return "redirect:/admin/dashboard";
//             }
//             }
//                 return "redirect:/shop";
//         }


//     @GetMapping("/signup")
//    public String Login(){
//         Authentication securityAuthentication = SecurityContextHolder.getContext().getAuthentication();
//         Set<String> roles = AuthorityUtils.authorityListToSet(securityAuthentication.getAuthorities());
//
//         System.out.println(roles);
//
//         if(!(securityAuthentication instanceof AnonymousAuthenticationToken)){
//             if(roles.contains(Role.ADMIN.name())){
//                 return "redirect:/admin/dashboard";
//             }
//             else{
//                 return "redirect:/shop";
//             }
//         }
//         return "redirect:/login";
//     }

}
