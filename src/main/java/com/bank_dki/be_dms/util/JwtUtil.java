package com.bank_dki.be_dms.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private Key signingKey;
    
    @PostConstruct
    public void init() {
        try {
            // Decode Base64-encoded secret
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            
            // Validate key length (HS256 requires at least 256 bits = 32 bytes)
            if (keyBytes.length < 32) {
                throw new IllegalStateException(
                        String.format("JWT secret key is too short. Expected at least 32 bytes (256 bits), got %d bytes. "
                                + "Please provide a Base64-encoded 32-byte key.", keyBytes.length)
                );
            }
            
            // Create signing key
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "JWT secret must be a valid Base64-encoded string. Please check your jwt.secret configuration.", e
            );
        }
    }
    
    private Key getSigningKey() {
        return signingKey;
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}