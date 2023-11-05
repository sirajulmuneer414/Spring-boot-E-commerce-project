package com.sirajul.lenscraft.Controller;

import com.sirajul.lenscraft.DTO.UserInformationDto;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.entity.user.enums.UserStatus;
import com.sirajul.lenscraft.mapping.UserInformationMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@Slf4j
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
        return "admin/dashboard";
    }

    @GetMapping("/customers")
    public String getCustomers(Model model, @RequestParam(name = "search" ,required = false) String keyword){
        List<UserInformationDto> users;

        if(keyword==null) {
            users = userService.findAllUsers();
        }else{
            users = userService.findAllUsersContaining(keyword);
        }
        for(UserInformationDto user : users){
            System.out.println("user "+user.getFirstName()+" profilePic "+user.getProfilePic());
        }

        model.addAttribute("userList",users);

        return "admin/customerlist";

    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id")UUID id){

        userService.deleteUser(id);

        return "redirect:/admin/customers";

    }

    @GetMapping("/customers/update/{id}")
    public String getUpdate(@PathVariable("id")UUID id,Model model){
        UserInformation user = userService.findById(id);
        UserInformationDto userDto = userInfoMapping.repoToAdminMapping(user);
        List<String> roles = new ArrayList<>();
        for(Role role : Role.values()){

            roles.add(role.name());

        }
        List<String> statuses = new ArrayList<>();
        for(UserStatus status : UserStatus.values()){

            statuses.add(status.name());

        }
        model.addAttribute("roles",roles);
        model.addAttribute("statuses",statuses);
        model.addAttribute("usertoupdate",userDto);

        return "admin/updateuser";
    }

    @PostMapping("/customers/update/{id}")
    public String update(@ModelAttribute() UserInformationDto userDto){

        log.info("inside post update user");



        userService.updateUserById(userDto);

        return "redirect:/admin/customers";
        
    }

    @GetMapping("/customers/block/{id}")
    public String blockUser(@PathVariable("id")UUID id){

        userService.blockUserById(id);

        return "redirect:/admin/customers";
    } @GetMapping("/customers/unblock/{id}")
    public String unblockUser(@PathVariable("id")UUID id){

        userService.unBlockUserById(id);

        return "redirect:/admin/customers";
    }



}
