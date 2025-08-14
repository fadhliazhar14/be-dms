package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.JwtResponse;
import com.bank_dki.be_dms.dto.LoginRequest;
import com.bank_dki.be_dms.dto.MessageResponse;
import com.bank_dki.be_dms.dto.SignupRequest;
import com.bank_dki.be_dms.entity.Role;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.repository.RoleRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import com.bank_dki.be_dms.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword())
        );
        
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userPrincipal);
        
        User user = userRepository.findByUsernameOrEmailWithRoles(
                loginRequest.getUsernameOrEmail()
        ).orElseThrow(() -> new RuntimeException("User not found"));
        
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        
        return new JwtResponse(jwt, user.getUsername(), user.getEmail(), roles);
    }
    
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }
        
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }
        
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        
        if (strRoles == null || strRoles.isEmpty()) {
            Role operatorRole = roleRepository.findByName("OPERATOR")
                    .orElseThrow(() -> new RuntimeException("Error: OPERATOR role is not found."));
            roles.add(operatorRole);
            } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName("ADMIN")
                                .orElseThrow(() -> new RuntimeException("Error: ADMIN role is not found."));
                        roles.add(adminRole);
                        break;
                    case "OPERATOR":
                        Role operatorRole = roleRepository.findByName("OPERATOR")
                                .orElseThrow(() -> new RuntimeException("Error: OPERATOR role is not found."));
                        roles.add(operatorRole);
                        break;
                    default:
                        throw new RuntimeException("Error: Role " + role + " is not found.");
                }
            });
        }
        
        user.setRoles(roles);
        userRepository.save(user);
        
        return new MessageResponse("User registered successfully!");
    }
}