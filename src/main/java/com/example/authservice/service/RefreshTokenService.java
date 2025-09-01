package com.example.authservice.service;

import com.example.authservice.entities.RefreshToken;
import com.example.authservice.entities.UserInfo;
import com.example.authservice.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.authservice.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserRepository userRepository;
    @Transactional
    public RefreshToken createRefreshToken(String username){
        UserInfo userInfo = userRepository.findByUsername(username);
        
        // Delete any existing refresh tokens for this user
        deleteByUser(userInfo);
        
        RefreshToken refreshToken = RefreshToken.builder()
            .userInfo(userInfo)
            .token(UUID.randomUUID().toString())
            .expiryDate(Instant.now().plusMillis(600000))
            .build();
        return refreshTokenRepository.save(refreshToken);

    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if (token.getExpiryDate().compareTo(Instant.now())<0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " is expired , make a new login ");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteByUser(UserInfo userInfo){
        refreshTokenRepository.deleteByUserInfo(userInfo);
    }

    public Optional<RefreshToken> findByUser(UserInfo userInfo){
        return refreshTokenRepository.findByUserInfo(userInfo);
    }
}
