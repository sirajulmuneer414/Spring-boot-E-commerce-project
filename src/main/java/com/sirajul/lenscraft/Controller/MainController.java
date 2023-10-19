package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.SignupDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

     @GetMapping("/Register")
        public ResponseEntity<String> getRegister(Model model){

         SignupDto signupDto = new SignupDto();
         model.addAttribute("user",signupDto);

         return new ResponseEntity<>("signup",HttpStatus.OK);
        }
     @PostMapping("/Register")
        public ResponseEntity<String> registerUser(@ModelAttribute )
}
