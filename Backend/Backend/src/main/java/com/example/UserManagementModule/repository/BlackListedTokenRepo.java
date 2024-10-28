package com.example.UserManagementModule.repository;

import com.example.UserManagementModule.entity.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListedTokenRepo extends JpaRepository<BlackListedToken, String> {
    Optional<BlackListedToken> findByToken(String token);
}
