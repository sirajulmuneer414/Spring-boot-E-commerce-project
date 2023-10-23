package com.sirajul.lenscraft.mapping;

import com.sirajul.lenscraft.DTO.SignupDto;
import com.sirajul.lenscraft.entity.user.UserInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserInformationMapping {
    @Autowired
    PasswordEncoder passwordEncoder;

    public UserInformation signupDtoMapping(SignupDto signupDto){
        UserInformation user = new UserInformation();

        user.setEmailId(signupDto.getEmailId());
        user.setFirstName(signupDto.getFirstName());
        user.setLastName(signupDto.getLastName());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setProfilePic(signupDto.getProfilePic());
        user.setUserCreated(LocalDateTime.now());
        user.setIsActive(true);

        return user;
    }


}
