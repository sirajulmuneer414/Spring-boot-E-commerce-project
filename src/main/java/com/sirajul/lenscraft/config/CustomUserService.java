package com.sirajul.lenscraft.config;

import com.sirajul.lenscraft.Repository.UserRepository;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.UserStatus;
import com.sirajul.lenscraft.exception.BlockedUserFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class CustomUserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInformation user = userRepository.findByEmailId(username);

        if(user == null){
            throw new UsernameNotFoundException(username);
        }
        else if(user.getUserStatus().equals(UserStatus.BLOCKED)){
            throw new BlockedUserFoundException("The user " + username + " is Blocked");
        }
        return new CustomUserDetailsService(user);
    }
}
