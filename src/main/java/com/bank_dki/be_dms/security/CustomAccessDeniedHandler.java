package com.bank_dki.be_dms.security;

import com.bank_dki.be_dms.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom AccessDeniedHandler that returns a consistent JSON response
 * for 403 Forbidden errors using ApiResponse format
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        log.warn("Access denied for request: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        ApiResponse<?> apiResponse = ApiResponse.error(
                HttpServletResponse.SC_FORBIDDEN,
                "Access denied. You do not have permission to access this resource.",
                null
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
