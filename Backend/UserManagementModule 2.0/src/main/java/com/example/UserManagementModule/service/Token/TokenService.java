package com.example.UserManagementModule.service.Token;

import com.example.UserManagementModule.entity.Token.JwtToken;
import com.example.UserManagementModule.repository.Token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public JwtToken saveToken(JwtToken token) {
        return tokenRepository.save(token);
    }
    public JwtToken getToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void deleteToken(String token) {
        JwtToken jwtToken = tokenRepository.findByToken(token);
        tokenRepository.deleteById(jwtToken.getId());
    }
}
