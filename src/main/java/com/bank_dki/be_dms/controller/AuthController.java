package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.*;
import com.bank_dki.be_dms.exception.BusinessValidationException;
import com.bank_dki.be_dms.service.AuthService;
import com.bank_dki.be_dms.service.RefreshTokenService;
import com.bank_dki.be_dms.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse response = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshTokenService::rotateRefreshToken)
                .map(newToken -> {
                    String username = newToken.getUser().getUserName();
                    JwtResponse.JwtResponseForRefresh accessToken = authService.generateNewAccessToken(username);
                    return ResponseEntity.ok(
                            new TokenRefreshResponse(
                                    accessToken.getAccessToken(),
                                    accessToken.getAccessTokenExpiry(),
                                    newToken.getToken(),
                                    newToken.getExpiryDate().getEpochSecond()
                            )
                    );
                })
                .orElseThrow(() -> new BusinessValidationException("Invalid refresh token"));
    }
}