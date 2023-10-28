package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.DTO.SignupDto;
import com.sirajul.lenscraft.DTO.UserInformationDto;
import com.sirajul.lenscraft.Repository.UserRepository;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.entity.user.enums.UserStatus;
import com.sirajul.lenscraft.exception.InvalidOtpException;
import com.sirajul.lenscraft.mapping.UserInformationMapping;
import com.sirajul.lenscraft.utils.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    OtpUtil otpUtil;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired
    UserInformationMapping userInformationMapping;

    public boolean userExistsByEmail(String emailId){


        return userRepository.existsByEmailId(emailId);

    }

    @Override
    public boolean verifyOtpAndSave(SignupDto signupDto, String otp) {

        UserInformationMapping mapping = new UserInformationMapping();
        if(signupDto != null && otpUtil.isOtpValid(signupDto, otp)){

            UserInformation user = mapping.signupDtoMapping(signupDto);

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if(userRepository.count() == 0)
                user.setRole(Role.ADMIN);
            else user.setRole(Role.USER);

            userRepository.save(user);
            otpUtil.sendEmail(signupDto.getEmailId());
            return true;
        }else{
            throw new InvalidOtpException("Invalid OTP Entry");
        }

    }

    @Override
    public List<UserInformationDto> findAllUsers() {
        List<UserInformation> users = userRepository.findByRole(Role.USER.name());
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

        user = userInformationMapping.adminToRepoUpdateMapping(userDto,user);

        userRepository.save(user);
    }

    @Override
    public void blockUserById(UUID id) {

        UserInformation user = userRepository.findById(id).get();

        user.setUserStatus(UserStatus.BLOCKED);

        userRepository.save(user);
    }

    @Override
    public List<UserInformationDto> findAllUsersContaining(String keyword) {
        return null;
    }

}
