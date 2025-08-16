package com.example.authservice.controller;

import com.example.authservice.request.RefreshTokenRequestDto;
import com.example.authservice.response.JwtResponseDto;
import com.example.authservice.service.jwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.entities.RefreshToken;
import com.example.authservice.request.AuthRequestDto;
import com.example.authservice.service.RefreshTokenService;

@RestController
public class TokenController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private jwtService jwtService;

    @PostMapping("/auth/v1/login")
    public ResponseEntity<?> AuthenticateAndGetToken(@RequestBody AuthRequestDto authRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));
        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDto.getUsername());
            return new ResponseEntity<>(JwtResponseDto.builder().accessToken(jwtService.generateToken(authRequestDto.getUsername())).token(refreshToken.getToken()).build(), HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/auth/v1/refresh")
    public JwtResponseDto refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto){

        return refreshTokenService.findByToken(refreshTokenRequestDto.getToken()).map(refreshTokenService::verifyExpiration).map(RefreshToken::getUserInfo).map(userInfo -> {
            String token = jwtService.generateToken(userInfo.getUsername());
            return JwtResponseDto.builder().accessToken(token).token(refreshTokenRequestDto.getToken()).build();
        }).orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

    }



}
