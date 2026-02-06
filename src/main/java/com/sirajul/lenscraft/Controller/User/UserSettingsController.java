package com.sirajul.lenscraft.Controller.User;

import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.Address;
import com.sirajul.lenscraft.entity.user.UserInformation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/user/profile")
public class UserSettingsController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    com.sirajul.lenscraft.Service.interfaces.CloudinaryService cloudinaryService;

    @GetMapping()
    public String getUserSettings(
            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();

        UserInformation user = userService.findByEmailId(username);

        List<Address> addressList = user.getAddresses();
        List<Address> addresses = new ArrayList<>();
        if (addressList != null && !addressList.isEmpty()) {
            for (Address add : user.getAddresses()) {

                if (add.isActive()) {
                    addresses.add(add);
                }
            }
        }

        model.addAttribute("user", user);

        model.addAttribute("username", username);

        model.addAttribute("addressToEdit", new Address());

        model.addAttribute("addresses", addresses);

        return "user/profile";
    }

    @GetMapping("/change-password")
    public String changePassword() {

        return "user/change-password";

    }

    @PostMapping("/change-password")
    public String postChangePassword(
            @RequestParam("oldPassword") String passwordOld,
            @RequestParam("newPassword") String passwordNew,
            @RequestParam("userId") UUID userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserInformation user = userService.findById(userId);

        if (passwordEncoder.matches(passwordOld, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwordNew));
            userService.save(user);
            redirectAttributes.addFlashAttribute("changePasswordSuccess", "The Password changed successfully");
        } else {
            redirectAttributes.addFlashAttribute("changePasswordFailed",
                    "OOps ! Current password you have entered is WRONGGG!");
        }

        return "redirect:/user/profile";
    }

    @PostMapping("/edit")
    public String editUserProfile(
            @RequestParam("userId") UUID userId,
            @ModelAttribute("user") UserInformation userEdit,
            @RequestParam(name = "image", required = false) org.springframework.web.multipart.MultipartFile image)
            throws java.io.IOException {

        UserInformation user = userService.findById(userId);

        if (Objects.nonNull(userEdit.getFirstName()) && "" != userEdit.getFirstName()) {
            user.setFirstName(userEdit.getFirstName());
        }
        if (Objects.nonNull(userEdit.getLastName()) && "" != userEdit.getLastName()) {
            user.setLastName(userEdit.getLastName());
        }

        if (image != null && !image.isEmpty()) {
            String folder = "lenscraft/profileImages/" + user.getEmailId();
            String url = cloudinaryService.uploadFile(image, folder);
            user.setProfilePic(url);
        }

        userService.save(user);

        return "redirect:/user/profile";

    }

    @PostMapping("/add-address")
    public String addAddress(
            @ModelAttribute("address") Address address,
            @RequestParam("fullName") String fullName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("buildingName") String buildingName,
            @RequestParam("streetName") String streetName,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("postalCode") String postalCode,
            RedirectAttributes redirectAttributes) {

        // Validation for 10-digit mobile number
        if (!phoneNumber.matches("\\d{10}")) {
            redirectAttributes.addFlashAttribute("error", "Mobile number must be exactly 10 digits.");
            return "redirect:/user/profile";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInformation user = userService.findByEmailId(auth.getName());

        address.setBuyerName(fullName);
        address.setMobileNumber(phoneNumber);
        address.setHouseAddress(buildingName + ", " + streetName); // Combining for now as per entity probably
        address.setDistrict(city);
        address.setState(state);
        address.setPincode(postalCode);
        address.setActive(true);
        address.setUser(user);

        List<Address> addresses = user.getAddresses();
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        addresses.add(address);
        user.setAddresses(addresses);

        userService.save(user); // Cascading save

        redirectAttributes.addFlashAttribute("success", "Address added successfully!");
        return "redirect:/user/profile";
    }

}
