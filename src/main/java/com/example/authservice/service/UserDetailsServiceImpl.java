package com.example.authservice.service;

import com.example.authservice.EventProducer.UserInfoProducer;
import com.example.authservice.model.UserDetailsDto;
import com.example.authservice.model.UserSignUpEventDto;

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
    UserInfoProducer userInfoProducer;
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
        // Check if email is null or empty
        if (userDetailsDto.getEmail() == null || userDetailsDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        if (!userDetailsDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        userDetailsDto.setPassword(passwordEncoder.encode(userDetailsDto.getPassword()));
        if (Objects.nonNull(checkIfUserAlreadyExists(userDetailsDto))) {
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId, userDetailsDto.getFirstName(), userDetailsDto.getLastName(), userDetailsDto.getUsername(), userDetailsDto.getPassword(), userDetailsDto.getEmail(), new HashSet<>()));

        UserSignUpEventDto event = UserSignUpEventDto.builder()
                .userId(userId)
                .firstName(userDetailsDto.getFirstName())
                .lastName(userDetailsDto.getLastName())
                .email(userDetailsDto.getEmail())
                .build();
        userInfoProducer.sendEventToKafka(event);
        return true; 
    }

    
}
