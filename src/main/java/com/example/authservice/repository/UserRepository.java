package com.example.authservice.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.authservice.entities.UserInfo;

public interface UserRepository extends CrudRepository<UserInfo, String> {

    public UserInfo findByUsername(String username);


}
