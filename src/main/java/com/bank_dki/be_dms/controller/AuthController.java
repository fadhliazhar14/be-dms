package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.*;
import com.bank_dki.be_dms.exception.BusinessValidationException;
import com.bank_dki.be_dms.service.AuthService;
import com.bank_dki.be_dms.service.RefreshTokenService;
import com.bank_dki.be_dms.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@RequestBody SignupRequest signUpRequest) {
        try {
            MessageResponse response = authService.registerUser(signUpRequest);
            if (response.getMessage().startsWith("Error:")) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshTokenService::rotateRefreshToken)
                .map(newToken -> {
                    String username = newToken.getUser().getUserName();
                    JwtResponse.JwtResponseForRefresh accessToken = authService.generateNewAccessToken(username);
                    return ResponseEntity.ok(
                            new TokenRefreshResponse(
                                    accessToken.getAccesstoken(),
                                    accessToken.getAcessTokenExpiry(),
                                    newToken.getToken(),
                                    newToken.getExpiryDate().getEpochSecond()
                            )
                    );
                })
                .orElseThrow(() -> new BusinessValidationException("Invalid refresh token"));
    }
}