package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.SignupDto;
import com.sirajul.lenscraft.Service.UserServiceImp;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.exception.InvalidOtpException;
import com.sirajul.lenscraft.utils.FileUploadUtil;
import com.sirajul.lenscraft.utils.OtpUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
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
        public String registerUser(@ModelAttribute("user") SignupDto signupDto, @RequestParam("file")MultipartFile file, HttpSession httpSession, RedirectAttributes redirectAttributes) throws IOException {

        boolean userExists = userService.userExistsByEmail(signupDto.getEmailId());

        if(userExists){
            String errorMessage = "The Email Id you have entered already exists";
            redirectAttributes.addFlashAttribute("EmailError",errorMessage);

            return "redirect:/register";
        }

        if(!file.isEmpty()){
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            signupDto.setProfilePic(fileName);

            String upload = "profilePic/" + signupDto.getEmailId();

            FileUploadUtil.saveFile(upload, fileName, file);

        }

        String otp = otpUtil.generateOtp();
        signupDto.setOtp(otp);
        signupDto.setOtpGeneratedTime(LocalDateTime.now());

        otpUtil.sendOtpEmail(signupDto.getEmailId(),otp);
        httpSession.setAttribute("Non-verifiedUser",signupDto);


         return "redirect:/verification";
     }

     @GetMapping("/verification")
    public String getVerification(){
            return "verification";
     }

     @PostMapping("/verification")
    public  String verification(@RequestParam("otp") String otp, HttpSession httpSession,
                               Model model){
            SignupDto user = (SignupDto) httpSession.getAttribute("Non-verifiedUser");
        try {
            if (userService.verifyOtpAndSave(user, otp)) {
                return "redirect:/login?message=success";
            }
        }catch (InvalidOtpException e){
            model.addAttribute("otpError",e.getMessage()+", Please Try Again");
        }
            return "verification";
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

}
