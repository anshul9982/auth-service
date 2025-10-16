package com.example.authservice.controller;
import com.example.authservice.entities.RefreshToken;
import com.example.authservice.model.UserDetailsDto;
import com.example.authservice.response.JwtResponseDto;
import com.example.authservice.service.RefreshTokenService;
import com.example.authservice.service.UserDetailsServiceImpl;
import com.example.authservice.service.jwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    jwtService jwtService;
    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUpUser(@RequestBody UserDetailsDto userDetailsDto) {
        try {
            Boolean isSignedUp = userDetailsService.signupUser(userDetailsDto);
            if(Boolean.FALSE.equals(isSignedUp)){
                return new ResponseEntity<>("User " + userDetailsDto.getUsername() + " already exists", HttpStatus.BAD_REQUEST);
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetailsDto.getUsername());
            String jwtToken = jwtService.generateToken(userDetailsDto.getUsername());
            return new ResponseEntity<>(JwtResponseDto.builder().accessToken(jwtToken).token(refreshToken.getToken()).build(), HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>("Exception in User Service " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


}
