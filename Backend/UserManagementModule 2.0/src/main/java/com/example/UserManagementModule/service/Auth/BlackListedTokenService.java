package com.example.UserManagementModule.service.Auth;

import com.example.UserManagementModule.entity.BlackListedToken;
import com.example.UserManagementModule.repository.BlackListedTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class BlackListedTokenService {

    @Autowired
    private BlackListedTokenRepo blackListedTokenRepo;

    public void blacklistToken(String token, Date expiryDate) {
        BlackListedToken blacklistedToken = new BlackListedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpiryDate(expiryDate);
        blackListedTokenRepo.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        Optional<BlackListedToken> blacklistedToken = blackListedTokenRepo.findByToken(token);
        return blacklistedToken.isPresent();
    }
}
