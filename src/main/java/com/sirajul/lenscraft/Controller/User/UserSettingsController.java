package com.sirajul.lenscraft.Controller.User;

import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.UserInformation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user/settings")
public class UserSettingsController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping()
    public String getUserSettings(
            Model model,
            @RequestParam("username")String emailId
    ){
        UserInformation user = userService.findByEmailId(emailId);

        model.addAttribute("user",user);

        model.addAttribute("username",user.getEmailId());

        return "user/settings";
    }

    @GetMapping("/change-password")
    public String changePassword(){

        return "user/change-password";

    }

    @PostMapping("/change-password")
    public String postChangePassword(
            @RequestParam("oldPassword") String passwordOld,
            @RequestParam("username") String username,
            HttpSession session
    ) {

        UserInformation user = userService.findByEmailId(username);

        System.out.println(passwordEncoder.matches(passwordOld,user.getPassword()));

        return "user/password-new";
    }

}
