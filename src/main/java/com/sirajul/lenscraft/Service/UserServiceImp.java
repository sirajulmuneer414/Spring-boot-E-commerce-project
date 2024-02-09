package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.DTO.SignupDto;
import com.sirajul.lenscraft.DTO.UserInformationDto;
import com.sirajul.lenscraft.Repository.UserRepository;
import com.sirajul.lenscraft.Service.interfaces.CartService;
import com.sirajul.lenscraft.Service.interfaces.ReferralOfferService;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.Service.interfaces.WishlistService;
import com.sirajul.lenscraft.entity.user.*;
import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import com.sirajul.lenscraft.exception.InvalidOtpException;
import com.sirajul.lenscraft.mapping.UserInformationMapping;
import com.sirajul.lenscraft.utils.OtpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImp implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    OtpUtil otpUtil;

    @Autowired
    ReferralOfferService referralOfferService;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired
    UserInformationMapping userInformationMapping;

    @Autowired
    WishlistService wishlistService;

    @Autowired
    CartService cartService;

    public boolean userExistsByEmail(String emailId){


        return userRepository.existsByEmailId(emailId);

    }

    @Override
    public boolean verifyOtpAndSave(SignupDto signupDto, String otp) {


        if(signupDto != null && otpUtil.isOtpValid(signupDto, otp)){

            UserInformation user = userInformationMapping.signupDtoMapping(signupDto);

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            Random rand = new Random();

            String referralCodeNumPart = String.valueOf(rand.nextInt(999999)+100000);

            String referralFirstPart = user.getFirstName().substring(0,3).toUpperCase();

            user.setReferralCode(referralFirstPart+referralCodeNumPart);

            if(signupDto.getRole()==Role.ADMIN.name())
                user.setRole(Role.ADMIN);
            else user.setRole(Role.USER);

            user = userRepository.save(user);

            Wishlist wishlist = new Wishlist();
            wishlist.setProducts(new ArrayList<>());
            wishlist.setUser(user);
            wishlistService.save(wishlist);

            user.setWishlist(wishlist);

            Cart cart = new Cart();
            cart.setCartedItems(new ArrayList<>());
            cart.setUser(user);
            cartService.save(cart);

            user.setOrders(new ArrayList<Order>());

            user.setCart(cart);

            user.setAddresses(new ArrayList<Address>());

            user = userRepository.save(user);

            if(userRepository.existsByReferralCodeIgnoreCase(signupDto.getReferralCode())) {
                UserInformation userReferred = userRepository.findByReferralCodeIgnoreCase(signupDto.getReferralCode());
                referralOfferService.assignMoneyToWallets(user, userReferred);
            }

            otpUtil.sendEmail(signupDto.getEmailId());
            return true;
        }else{
            throw new InvalidOtpException("Invalid OTP Entry");
        }

    }

    @Override
    public List<UserInformationDto> findAllUsers() {
        List<UserInformation> users = userRepository.findByRole(Role.USER);
        System.out.println(users.isEmpty());
        return userInformationMapping.listMapping(users);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserInformation findById(UUID id) {
        return userRepository.findById(id).get();
    }

    @Override
    public void updateUserById(UserInformationDto userDto) {
        UserInformation user = userRepository.findById(userDto.getId()).get();

        if(user != null){
            log.info("user is found from the repository");
        }

        user = userInformationMapping.adminToRepoUpdateMapping(userDto,user);

        System.out.println(user.getLastName());

        userRepository.save(user);
    }

    @Override
    public void blockUserById(UUID id) {

        UserInformation user = userRepository.findById(id).get();

        user.setActiveStatus(ActiveStatus.BLOCKED);

        userRepository.save(user);
    }

    @Override
    public List<UserInformationDto> findAllUsersContaining(String keyword) {
        return userRepository.findAllByFirstNameContaining(keyword);
    }

    @Override
    public void unBlockUserById(UUID id) {
        UserInformation user = userRepository.findById(id).get();

        user.setActiveStatus(ActiveStatus.ACTIVE);

        userRepository.save(user);

    }

    @Override
    public UserInformation findByEmailId(String name) {
        return userRepository.findByEmailId(name);
    }

    @Override
    public void save(UserInformation user) {
        userRepository.save(user);
    }

    @Override
    public Long countOfUsers() {

       return userRepository.countByRole(Role.USER);

    }

    @Override
    public boolean existsByReferralCode(String code) {
        return userRepository.existsByReferralCodeIgnoreCase(code);
    }

}
