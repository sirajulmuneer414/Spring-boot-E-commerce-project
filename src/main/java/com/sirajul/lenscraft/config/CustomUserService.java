package com.sirajul.lenscraft.config;

import com.sirajul.lenscraft.Repository.UserRepository;
import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import com.sirajul.lenscraft.exception.BlockedUserFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


    log.info("inside load userrname");
        UserInformation user = userRepository.findByEmailId(username);
        System.out.println(user.getEmailId());

        if(user == null){
            log.info("username not found exception");
            throw new UsernameNotFoundException(username);
        }
        else if(user.getActiveStatus().equals(ActiveStatus.BLOCKED)){
            throw new BlockedUserFoundException("The user " + username + " is Blocked");
        }
        log.info("no issue in load");
        return new CustomUserDetailsService(user);
    }
}
