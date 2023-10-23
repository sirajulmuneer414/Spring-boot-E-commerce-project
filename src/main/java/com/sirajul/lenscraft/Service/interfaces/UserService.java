package com.sirajul.lenscraft.Service.interfaces;


import com.sirajul.lenscraft.DTO.SignupDto;

public interface UserService {

    boolean userExistsByEmail(String EmailId);

    boolean verifyOtpAndSave(SignupDto signupDto, String otp);
}
