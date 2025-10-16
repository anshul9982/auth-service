package com.example.authservice.service;

import com.example.authservice.entities.UserInfo;
import com.example.authservice.entities.UserRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends UserInfo implements UserDetails {

    private String username;
    private String password;

    Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserInfo user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.userId = user.getUserId();

        List<GrantedAuthority> auths = new ArrayList<>();
        for (UserRoles roles : user.getRoles()){
            auths.add(new SimpleGrantedAuthority(roles.getName().toUpperCase()));
        }
        this.authorities = auths;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getUserId() {
        return userId;
    }
}
