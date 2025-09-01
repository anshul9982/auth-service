package com.example.authservice.repository;

import com.example.authservice.entities.RefreshToken;
import com.example.authservice.entities.UserInfo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByUserInfo(UserInfo userInfo);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.userInfo = :userInfo")
    void deleteByUserInfo(@Param("userInfo") UserInfo userInfo);

}
