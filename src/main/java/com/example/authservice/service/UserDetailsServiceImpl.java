package com.example.authservice.service;

import com.example.authservice.model.UserDetailsDto;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.authservice.entities.UserInfo;
import com.example.authservice.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserInfo user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExists(UserDetailsDto userDetailsDto){
        return userRepository.findByUsername(userDetailsDto.getUsername());
    }

    public boolean signupUser(UserDetailsDto userDetailsDto){
        if (!userDetailsDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        userDetailsDto.setPassword(passwordEncoder.encode(userDetailsDto.getPassword()));
        if (Objects.nonNull(checkIfUserAlreadyExists(userDetailsDto))) {
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId, userDetailsDto.getFirstName(), userDetailsDto.getLastName(), userDetailsDto.getUsername(), userDetailsDto.getPassword(), userDetailsDto.getEmail(), new HashSet<>()));
        return true; 
    }

    
}
