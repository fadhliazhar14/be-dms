package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.JwtResponse;
import com.bank_dki.be_dms.dto.LoginRequest;
import com.bank_dki.be_dms.dto.MessageResponse;
import com.bank_dki.be_dms.dto.SignupRequest;
import com.bank_dki.be_dms.entity.RefreshToken;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.repository.RoleRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import com.bank_dki.be_dms.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserEmail(),
                        loginRequest.getUserHashPassword())
        );
        
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userPrincipal);


        User user = userRepository.findByUserNameOrEmailWithRole(
                loginRequest.getUserEmail()
        ).orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

        String role = user.getRole() != null ? user.getRole().getRoleName() : "OPERATOR";
        long acessTokenExpiredAt = jwtUtil.extractExpiration(jwt).getTime() / 1000;
        long refreshTokenExpiredAt = refreshToken.getExpiryDate().getEpochSecond();

        return new JwtResponse(jwt,
                refreshToken.getToken(),
                "Bearer",
                user.getUserName(),
                user.getUserEmail(),
                role,
                acessTokenExpiredAt,
                refreshTokenExpiredAt
        );
    }
    
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUserName())) {
            return new MessageResponse("Error: Username is already taken!");
        }
        
        if (userRepository.existsByUserEmail(signUpRequest.getUserEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }
        
        // Validate if role exists
        if (signUpRequest.getRoleId() != null) {
            roleRepository.findById(signUpRequest.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Error: Role with ID " + signUpRequest.getRoleId() + " is not found."));
        }
        
        User user = new User();
        user.setUserName(signUpRequest.getUserName());
        user.setUserEmail(signUpRequest.getUserEmail());
        user.setUserHashPassword(passwordEncoder.encode(signUpRequest.getUserHashPassword()));
        user.setRoleId(signUpRequest.getRoleId() != null ? signUpRequest.getRoleId() : (short) 2); // Default to OPERATOR role
        user.setUserTglLahir(signUpRequest.getUserTglLahir());
        user.setUserJabatan(signUpRequest.getUserJabatan());
        user.setUserTempatLahir(signUpRequest.getUserTempatLahir());
        user.setUserIsActive(true);
        
        userRepository.save(user);
        
        return new MessageResponse("User registered successfully!");
    }

    public JwtResponse.JwtResponseForRefresh generateNewAccessToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateToken(userDetails);
        long newAccessTokenExpiry = jwtUtil.extractExpiration(newAccessToken).getTime() / 1000;
        return new JwtResponse.JwtResponseForRefresh(newAccessToken, newAccessTokenExpiry);
    }
}