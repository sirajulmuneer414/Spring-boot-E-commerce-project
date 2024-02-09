package com.sirajul.lenscraft.mapping;

import com.sirajul.lenscraft.DTO.SignupDto;
import com.sirajul.lenscraft.DTO.UserInformationDto;
import com.sirajul.lenscraft.Repository.UserRepository;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.Role;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserInformationMapping {

    @Autowired
    UserRepository userRepository;


    public UserInformation signupDtoMapping(SignupDto signupDto){
        UserInformation user = new UserInformation();

        user.setEmailId(signupDto.getEmailId());
        user.setFirstName(signupDto.getFirstName());
        user.setLastName(signupDto.getLastName());
        user.setPassword(signupDto.getPassword());
        user.setProfilePic(signupDto.getProfilePic());
        user.setUserCreated(LocalDateTime.now());
        user.setActiveStatus(ActiveStatus.ACTIVE);

        return user;
    }

    public UserInformationDto repoToAdminMapping(UserInformation user){
        UserInformationDto dto = new UserInformationDto();

        dto.setId(user.getUserId());
        dto.setEmailId(user.getEmailId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());

        System.out.println("the user profile pic name is "+user.getProfilePic());

        dto.setProfilePic(user.getProfilePic());
        dto.setUserStatus(user.getActiveStatus().name());
        dto.setRole(user.getRole().name());

        return dto;
    }

    public UserInformation adminToRepoUpdateMapping(UserInformationDto userDto,UserInformation user){

        log.info("Inside the mapping for dto to userInformation class");


        if(Objects.nonNull(userDto.getFirstName()) && !"".equalsIgnoreCase(userDto.getFirstName())){
        user.setFirstName(userDto.getFirstName());}

        if(Objects.nonNull(userDto.getLastName()) && !"".equalsIgnoreCase(userDto.getLastName())){
        user.setLastName(userDto.getLastName());}

        if(Objects.nonNull(userDto.getEmailId()) && !"".equalsIgnoreCase(userDto.getEmailId()) && !userRepository.existsByEmailId(userDto.getEmailId())){
            user.setEmailId(userDto.getEmailId());
        }

        switch (userDto.getRole()){
            case "ADMIN":
                user.setRole(Role.ADMIN);
                break;
            case "SELLER":
                user.setRole(Role.SELLER);
                break;
            default:
                user.setRole(Role.USER);
                break;
        }
        switch(userDto.getUserStatus()){
            case "BLOCKED":
                user.setActiveStatus(ActiveStatus.BLOCKED);
                break;
            default:
                user.setActiveStatus(ActiveStatus.ACTIVE);
        }
        return user;
    }


    public List<UserInformationDto> listMapping(List<UserInformation> users) {
        List<UserInformationDto> usersDto = new ArrayList<UserInformationDto>();
        for(UserInformation user:users){
            UserInformationDto dto = new UserInformationDto();

            dto.setId(user.getUserId());
            dto.setEmailId(user.getEmailId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setProfilePic(user.getProfilePic());
            dto.setUserStatus(user.getActiveStatus().name());
            dto.setRole(user.getRole().name());

            usersDto.add(dto);
        }
        return usersDto;
    }

    public List<UserInformation> listMappingToEntity(List<UserInformationDto> usersDto) {
        List<UserInformation> users = new ArrayList<>();
        for(UserInformationDto dto:usersDto){
            UserInformation user = new UserInformation();

            user.setUserId(dto.getId());
            user.setEmailId(dto.getEmailId());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            switch (dto.getRole()){
                case "ADMIN":
                    user.setRole(Role.ADMIN);
                    break;
                case "SELLER":
                    user.setRole(Role.SELLER);
                    break;
                default:
                    user.setRole(Role.USER);
                    break;
            }
            switch(dto.getUserStatus()){
                case "BLOCKED":
                    user.setActiveStatus(ActiveStatus.BLOCKED);
                    break;
                default:
                    user.setActiveStatus(ActiveStatus.ACTIVE);
            }

            usersDto.add(dto);
        }
        return users;
    }
}
