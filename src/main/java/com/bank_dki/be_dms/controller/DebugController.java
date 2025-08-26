package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.util.ApiResponse;
import com.bank_dki.be_dms.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {
    
    private final JwtUtil jwtUtil;
    
    /**
     * Debug endpoint to validate JWT token structure and content
     * This endpoint helps troubleshoot JWT token issues
     */
    @PostMapping("/jwt-validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateJwtToken(HttpServletRequest request) {
        Map<String, Object> debugInfo = new HashMap<>();
        
        try {
            String authHeader = request.getHeader("Authorization");
            debugInfo.put("authHeader", authHeader != null ? "Present" : "Missing");
            debugInfo.put("authHeaderLength", authHeader != null ? authHeader.length() : 0);
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                debugInfo.put("tokenLength", token.length());
                debugInfo.put("tokenEmpty", token.trim().isEmpty());
                
                // Check if token has proper JWT structure (2 dots)
                long dotCount = token.chars().filter(ch -> ch == '.').count();
                debugInfo.put("dotCount", dotCount);
                debugInfo.put("isProperJwtStructure", dotCount == 2);
                
                if (dotCount == 2 && !token.trim().isEmpty()) {
                    try {
                        // Try to extract claims using public methods
                        String username = jwtUtil.extractUsername(token);
                        Date expiration = jwtUtil.extractExpiration(token);
                        boolean isExpired = jwtUtil.isTokenExpired(token);
                        
                        debugInfo.put("username", username);
                        debugInfo.put("expirationDate", expiration);
                        debugInfo.put("isExpired", isExpired);
                        debugInfo.put("tokenValid", "Token structure is valid");
                    } catch (Exception e) {
                        debugInfo.put("tokenError", e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                } else {
                    debugInfo.put("tokenError", "Invalid JWT structure - expected format: header.payload.signature");
                }
            } else {
                debugInfo.put("authHeaderError", "Authorization header missing or doesn't start with 'Bearer '");
            }
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success("JWT Debug Information", debugInfo);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            debugInfo.put("unexpectedError", e.getClass().getSimpleName() + ": " + e.getMessage());
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(500, "Debug validation failed", debugInfo, null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Simple endpoint to check if request reaches the controller
     */
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping() {
        ApiResponse<String> response = ApiResponse.success("Debug endpoint is working", "pong");
        return ResponseEntity.ok(response);
    }
}
