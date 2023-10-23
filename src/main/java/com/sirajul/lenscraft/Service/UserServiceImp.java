package com.sirajul.lenscraft.Service;

import com.sirajul.lenscraft.DTO.SignupDto;
import com.sirajul.lenscraft.Repository.UserRepository;
import com.sirajul.lenscraft.Service.interfaces.UserService;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.exception.InvalidOtpException;
import com.sirajul.lenscraft.mapping.UserInformationMapping;
import com.sirajul.lenscraft.utils.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    OtpUtil otpUtil;

    public boolean userExistsByEmail(String emailId){


        return userRepository.ExistsByEmailId(emailId);

    }

    @Override
    public boolean verifyOtpAndSave(SignupDto signupDto, String otp) {

        UserInformationMapping mapping = new UserInformationMapping();
        if(signupDto != null && otpUtil.isOtpValid(signupDto, otp)){

            UserInformation user = mapping.signupDtoMapping(signupDto);

            userRepository.save(user);
            otpUtil.sendEmail(signupDto.getEmailId());
            return true;
        }else{
            throw new InvalidOtpException("Invalid OTP Entry");
        }

    }

}
