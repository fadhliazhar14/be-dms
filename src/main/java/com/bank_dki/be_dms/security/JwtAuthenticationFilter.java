package com.bank_dki.be_dms.security;

import com.bank_dki.be_dms.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // Skip JWT processing for public endpoints
        if (shouldSkipJwtProcessing(request)) {
            chain.doFilter(request, response);
            return;
        }
        
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            
            // Check if token is empty or just whitespace
            if (jwtToken == null || jwtToken.trim().isEmpty()) {
                logger.warn("JWT token is empty or null after removing Bearer prefix");
                chain.doFilter(request, response);
                return;
            }
            
            // Log token length for debugging (don't log actual token for security)
            logger.debug("Processing JWT token with length: " + jwtToken.length());
            
            try {
                // Extract username first
                username = jwtUtil.extractUsername(jwtToken);
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                logger.error("JWT token is malformed (invalid format). Token length: " + jwtToken.length() + 
                           ", Expected format: header.payload.signature", e);
                chain.doFilter(request, response);
                return;
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.warn("JWT token has expired for user: " + e.getClaims().getSubject());
                chain.doFilter(request, response);
                return;
            } catch (io.jsonwebtoken.UnsupportedJwtException e) {
                logger.error("JWT token is unsupported: " + e.getMessage());
                chain.doFilter(request, response);
                return;
            } catch (io.jsonwebtoken.SignatureException e) {
                logger.error("JWT token signature validation failed: " + e.getMessage());
                chain.doFilter(request, response);
                return;
            } catch (Exception e) {
                logger.error("Unable to parse JWT Token: " + e.getMessage(), e);
                chain.doFilter(request, response);
                return;
            }
        } else if (requestTokenHeader != null) {
            String headerPreview = requestTokenHeader.length() > 20 ? requestTokenHeader.substring(0, 20) + "..." : requestTokenHeader;
            logger.warn("Authorization header present but does not start with 'Bearer '. Header: " + headerPreview);
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validate token signature and expiry BEFORE setting authentication
                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    // Only set authentication if token is valid and not expired
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    logger.warn("JWT token validation failed for user: " + username);
                }
            } catch (Exception e) {
                logger.error("Error processing JWT token for user: " + username, e);
                // Don't set authentication if any error occurs during validation
            }
        }

        chain.doFilter(request, response);
    }
    
    /**
     * Determine if JWT processing should be skipped for public endpoints
     * @param request HTTP request
     * @return true if JWT processing should be skipped
     */
    private boolean shouldSkipJwtProcessing(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Public endpoints that don't require JWT authentication
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/setup/") ||
               path.startsWith("/api/debug/") ||
               path.equals("/api/test/all") ||
               path.equals("/api/roles/active");
    }
}