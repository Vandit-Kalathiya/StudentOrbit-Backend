package com.example.UserManagementModule.jwt;

import com.example.UserManagementModule.entity.BlackListedToken;
import com.example.UserManagementModule.repository.BlackListedTokenRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Component
public class JwtAuthenticationHelper {


	private final BlackListedTokenRepo blackListedTokenRepo;
	private String secret = "thisisacodingninjasdemonstrationforsecretkeyinspringsecurityjsonwebtokenauthentication";
	private static final long JWT_TOKEN_VALIDITY = 60*60*168;

	private final ObjectMapper objectMapper;

	public JwtAuthenticationHelper(BlackListedTokenRepo blackListedTokenRepo) {
		// Configure ObjectMapper to support Java 8 date/time
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		this.blackListedTokenRepo = blackListedTokenRepo;
	}
	
	public String getUsernameFromToken(String token)
	{
		Claims claims =  getClaimsFromToken(token);
		return claims.getSubject();
	}
	
	public Claims getClaimsFromToken(String token)
	{
		Claims claims = Jwts.parserBuilder().setSigningKey(secret.getBytes())
				.build().parseClaimsJws(token).getBody();
		return claims;
	}
	
	public Boolean isTokenExpired(String token)
	{
		Claims claims =  getClaimsFromToken(token);
		Date expDate = claims.getExpiration();
		return expDate.before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = createClaims(userDetails);
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(
						new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName()),
						SignatureAlgorithm.HS512
				)
				.serializeToJsonWith(claimsMap -> {
					try {
						return objectMapper.writeValueAsString(claimsMap).getBytes();
					} catch (JsonProcessingException e) {
						throw new IllegalArgumentException("Error serializing claims to JSON", e);
					}
				})
				.compact();
	}

	private Map<String, Object> createClaims(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		String username = userDetails.getUsername();

		if (username.startsWith("22")) {
			claims.put("role", "student");
		} else if ("ADMIN_1".equalsIgnoreCase(username)) {
			claims.put("role", "admin");
		} else {
			claims.put("role", "faculty");
		}
		System.out.println("--------------------------------------------------"+userDetails.getClass());
//		claims.put("userId", userDetails.getAuthorities());
		return claims;
	}


	private SecretKeySpec getSigningKey() {
		return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
	}

	private List<String> extractRoles(String token) {
		Claims claims = Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody();
		return claims.get("roles", List.class); // Extract the roles from claims
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimsFromToken(token).getExpiration();
	}

	public boolean isBlacklisted(String token) {
		Optional<BlackListedToken> blackListedToken = blackListedTokenRepo.findByToken(token);

        return blackListedToken.isPresent();
    }
}
