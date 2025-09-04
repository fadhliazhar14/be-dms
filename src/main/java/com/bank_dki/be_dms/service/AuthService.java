package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.*;
import com.bank_dki.be_dms.dto.mapper.UserMapper;
import com.bank_dki.be_dms.entity.RefreshToken;
import com.bank_dki.be_dms.entity.Role;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.exception.BusinessValidationException;
import com.bank_dki.be_dms.exception.ResourceNotFoundException;
import com.bank_dki.be_dms.repository.RoleRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import com.bank_dki.be_dms.util.CurrentUserUtils;
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
    private final CurrentUserUtils currentUserUtils;
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
        ).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

        String role = user.getRole() != null ? user.getRole().getRoleName() : "OPERATOR";
        long acessTokenExpiredAt = jwtUtil.extractExpiration(jwt).getTime() / 1000;
        long refreshTokenExpiredAt = refreshToken.getExpiryDate().getEpochSecond();

        return new JwtResponse(jwt,
                acessTokenExpiredAt,
                refreshToken.getToken(),
                refreshTokenExpiredAt,
                "Bearer",
                user.getUserName(),
                user.getUserEmail(),
                role
        );
    }
    
    public UserDTO registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUserName())) {
            throw  new BusinessValidationException("Error: Username is already taken!");
        }
        
        if (userRepository.existsByUserEmail(signUpRequest.getUserEmail())) {
            throw  new BusinessValidationException("Error: Email is already in use!");
        }
        
        // Determine role ID
        Short roleId;
        Role operatorRole;
        if (signUpRequest.getRoleId() != null) {
            // Validate if role exists
            operatorRole = roleRepository.findById(signUpRequest.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Error: Role with ID " + signUpRequest.getRoleId() + " is not found."));
            roleId = signUpRequest.getRoleId();
        } else {
            // Default to OPERATOR role
            operatorRole = roleRepository.findByRoleName("OPERATOR")
                    .orElseThrow(() -> new BusinessValidationException("Error: Default OPERATOR role not found. Please ensure roles are initialized properly."));
            roleId = operatorRole.getRoleId();
        }

        String currentUsername = currentUserUtils.getCurrentUsername();
        
        User user = new User();
        user.setUserName(signUpRequest.getUserName());
        user.setUserEmail(signUpRequest.getUserEmail());
        user.setUserHashPassword(passwordEncoder.encode(signUpRequest.getUserHashPassword()));
        user.setRoleId(roleId);
        user.setUserTglLahir(signUpRequest.getUserTglLahir());
        user.setUserJabatan(signUpRequest.getUserJabatan());
        user.setUserTempatLahir(signUpRequest.getUserTempatLahir());
        user.setUserIsActive(true);
        user.setUserCreateBy(currentUsername);
        
        userRepository.save(user);

        String formattedJobCode = String.format("%03d", user.getUserId());
        user.setUserJobCode("WI" + formattedJobCode);

        UserDTO dto = UserMapper.convertToDTO(userRepository.save(user));
        dto.setRoleName(operatorRole.getRoleName());

        return dto;
    }

    public JwtResponse.JwtResponseForRefresh generateNewAccessToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateToken(userDetails);
        long newAccessTokenExpiry = jwtUtil.extractExpiration(newAccessToken).getTime() / 1000;
        return new JwtResponse.JwtResponseForRefresh(newAccessToken, newAccessTokenExpiry);
    }
}