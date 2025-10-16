package com.example.authservice.controller;

import com.example.authservice.request.RefreshTokenRequestDto;
import com.example.authservice.response.JwtResponseDto;
import com.example.authservice.service.CustomUserDetails;
import com.example.authservice.service.UserDetailsServiceImpl;
import com.example.authservice.service.jwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.entities.RefreshToken;
import com.example.authservice.request.AuthRequestDto;
import com.example.authservice.service.RefreshTokenService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class TokenController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private jwtService jwtService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> AuthenticateAndGetToken(@RequestBody AuthRequestDto authRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));
        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDto.getUsername());
            return new ResponseEntity<>(JwtResponseDto.builder().accessToken(jwtService.generateToken(authRequestDto.getUsername())).token(refreshToken.getToken()).build(), HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/auth/refresh")
    public JwtResponseDto refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto){

        return refreshTokenService.findByToken(refreshTokenRequestDto.getToken()).map(refreshTokenService::verifyExpiration).map(RefreshToken::getUserInfo).map(userInfo -> {
            String token = jwtService.generateToken(userInfo.getUsername());
            return JwtResponseDto.builder().accessToken(token).token(refreshTokenRequestDto.getToken()).build();
        }).orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

    }

    @GetMapping("/auth/v1/ping")
    public ResponseEntity <Map<String, Object>> ping() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            String userId = customUserDetails.getUserId();
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            if (Objects.nonNull(userId)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    }

}
