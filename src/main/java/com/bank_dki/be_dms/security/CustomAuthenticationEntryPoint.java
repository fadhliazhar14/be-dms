package com.bank_dki.be_dms.security;

import com.bank_dki.be_dms.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom AuthenticationEntryPoint that returns a consistent JSON response
 * for 401 Unauthorized errors using ApiResponse format
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        log.warn("Authentication failed for request: {} - {}", request.getRequestURI(), authException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        ApiResponse<?> apiResponse = ApiResponse.error(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Authentication required. Please provide a valid JWT token.",
                null
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
