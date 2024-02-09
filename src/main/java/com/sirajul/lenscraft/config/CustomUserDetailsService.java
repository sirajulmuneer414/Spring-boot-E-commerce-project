package com.sirajul.lenscraft.config;

import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.ActiveStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetails {

    @Autowired
    UserInformation user;
     public CustomUserDetailsService(UserInformation user){
         log.info("Inside customerUserDetailservice constructor ");
        this.user = user;
         System.out.println(user.getPassword());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        log.info("insude");
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        return simpleGrantedAuthorities;
    }

    @Override
    public String getPassword() {
        log.info("on password");

        return user.getPassword();
    }

    @Override
    public String getUsername() {
        log.info("on username");
        return user.getEmailId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
         if(user.getActiveStatus().equals(ActiveStatus.BLOCKED)){
             return false;
         }
        return true;
    }
}
