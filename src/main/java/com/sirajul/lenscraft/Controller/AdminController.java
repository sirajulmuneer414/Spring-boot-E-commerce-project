package com.sirajul.lenscraft.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirajul.lenscraft.DTO.UserInformationDto;
import com.sirajul.lenscraft.Service.interfaces.OrderService;
import com.sirajul.lenscraft.Service.interfaces.OrderedItemsService;
import com.sirajul.lenscraft.Service.interfaces.ReferralOfferService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.offer.ReferralOffer;
import com.sirajul.lenscraft.entity.user.Order;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import com.sirajul.lenscraft.mapping.UserInformationMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    UserService userService;
    @Autowired
    UserInformationMapping userInfoMapping;

    @Autowired
    ReferralOfferService referralOfferService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderedItemsService orderedItemsService;
    @GetMapping()
    public String getDashboard(){

        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model
    ) throws JsonProcessingException {
        Long countOfUsers = userService.countOfUsers();
        model.addAttribute("totalCustomers",countOfUsers);

        List<Order> orders = orderService.findAllInOrderDelivered();

        Long totalSales = 0L;

        for(Order order : orders){
            totalSales += order.getTotalAmount();
        }

        model.addAttribute("totalSales",totalSales);

        Long items = orderedItemsService.countOfItems();

        model.addAttribute("soldProducts",items);
        Map<String,Double> weeklySales=orderService.getWeeklySales();
        Map<String,Long>weeklyCount=orderService.getWeeklyCount();
        Map<String,Double>dailySales=orderService.getDailySales();
        Map<String,Long>dailyCount=orderService.getDailyCount();
        Map<String,Double>monthlySales=orderService.getMonthlySales();
        Map<String,Long>monthlySalesCount=orderService.getMonthlySalesCount();
        Map<String,Double>yearlySales=orderService.getYearlySales();
        Map<String,Long>yearlySalesCount=orderService.getYearlySalesCount();
        ObjectMapper objectMapper=new ObjectMapper();
        model.addAttribute("yearlySales",objectMapper.writeValueAsString(yearlySales));
        model.addAttribute("yearlySalesCount",objectMapper.writeValueAsString(yearlySalesCount));
        model.addAttribute("monthlySales",objectMapper.writeValueAsString(monthlySales));
        model.addAttribute("monthlySalesCount",objectMapper.writeValueAsString(monthlySalesCount));
        model.addAttribute("dailySales",objectMapper.writeValueAsString(dailySales));
        model.addAttribute("dailyCount",objectMapper.writeValueAsString(dailyCount));
        model.addAttribute("weeklyCount",objectMapper.writeValueAsString(weeklyCount));
        model.addAttribute("weeklySales", objectMapper.writeValueAsString(weeklySales));

        return "admin/dashboard";
    }

    @GetMapping("/customers")
    public String getCustomers(Model model, @RequestParam(name = "keyword" ,required = false) String keyword){
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

        boolean referIsThere = false;

        ReferralOffer refer = new ReferralOffer();

        if(referralOfferService.isOfferAlreadyEstablished()){

            referIsThere = true;

            refer = referralOfferService.getTheReferalOffer();

        }

        model.addAttribute("referIsThere",referIsThere);
        model.addAttribute("refer",refer);

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
        for(ActiveStatus status : ActiveStatus.values()){

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
