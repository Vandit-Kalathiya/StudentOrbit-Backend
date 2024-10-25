package com.example.UserManagementModule.repository.Token;

import com.example.UserManagementModule.entity.Token.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<JwtToken, String> {

    JwtToken findByToken(String token);
}
