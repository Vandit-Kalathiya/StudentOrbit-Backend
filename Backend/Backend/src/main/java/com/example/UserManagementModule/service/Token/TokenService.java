package com.example.UserManagementModule.service.Token;

import com.example.UserManagementModule.entity.Token.JwtToken;
import com.example.UserManagementModule.repository.Token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

//    @CachePut(value = "tokens", key = "#token.id")
    public JwtToken saveToken(JwtToken token) {
        return tokenRepository.save(token);
    }

    @Cacheable(value = "tokens", key = "#token")
    public JwtToken getToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @CacheEvict(value = "tokens", key = "#token")
    public void deleteToken(String token) {
        JwtToken jwtToken = tokenRepository.findByToken(token);
        if (jwtToken != null) {
            tokenRepository.deleteById(jwtToken.getId());
        }
    }
}
