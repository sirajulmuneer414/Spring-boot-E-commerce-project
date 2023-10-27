package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.UserInformationDto;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.mapping.UserInformationMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;
    @Autowired
    UserInformationMapping userInfoMapping;
    @GetMapping("/")
    public String getDashboard(){

        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(){
        return "dashboard";
    }

    @GetMapping("/customers")
    public String getCustomers(Model model, @RequestParam(name = "search" ,required = false) String keyword){
        List<UserInformationDto> users;
        if(keyword==null) {
            users = userService.findAllUsers();
        }else{
            users = userService.findAllUsersContaining(keyword);
        }
        model.addAttribute("userList",users);

        return "admin/customerlist";

    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id")UUID id){

        userService.deleteUser(id);

        return "redirect:/customers";

    }

    @GetMapping("/customers/update/{id}")
    public String getUpdate(@PathVariable("id")UUID id,Model model){
        UserInformation user = userService.findById(id);
        UserInformationDto userDto = userInfoMapping.repoToAdminMapping(user);
        model.addAttribute("usertoupdate",userDto);

        return "admin/updateuser";
    }

    @PostMapping("/customers/update/{id}")
    public String update(@ModelAttribute UserInformationDto userDto){

        userService.updateUserById(userDto);

        return "redirect:/customers";
        
    }

    @GetMapping("/customers/block/{id}")
    public String blockUser(@PathVariable("id")UUID id){

        userService.blockUserById(id);

        return "redirect:/customers";
    }



}
