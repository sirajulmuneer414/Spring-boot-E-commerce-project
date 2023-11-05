package com.sirajul.lenscraft.Service.interfaces;


import com.sirajul.lenscraft.DTO.SignupDto;
import com.sirajul.lenscraft.DTO.UserInformationDto;
import com.sirajul.lenscraft.entity.user.UserInformation;

import java.util.List;
import java.util.UUID;

public interface UserService {

    boolean userExistsByEmail(String EmailId);

    boolean verifyOtpAndSave(SignupDto signupDto, String otp);

    List<UserInformationDto> findAllUsers();

    void deleteUser(UUID id);

    UserInformation findById(UUID id);

    void updateUserById(UserInformationDto userDto);

    void blockUserById(UUID id);

    List<UserInformationDto> findAllUsersContaining(String keyword);

    void unBlockUserById(UUID id);
}
