package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.MessageResponse;
import com.bank_dki.be_dms.entity.Role;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.repository.RoleRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SetupController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @PostMapping("/make-admin")
    public ResponseEntity<MessageResponse> makeUserAdmin(@RequestParam String email) {
        try {
            User user = userRepository.findByUsernameOrEmailWithRoles(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            // Check if user already has admin role
            boolean hasAdminRole = user.getRoles().stream()
                    .anyMatch(role -> "ADMIN".equals(role.getName()));
            
            if (hasAdminRole) {
                return ResponseEntity.ok(new MessageResponse("User already has ADMIN role"));
            }
            
            user.getRoles().add(adminRole);
            userRepository.save(user);
            
            return ResponseEntity.ok(new MessageResponse("ADMIN role assigned to user: " + email));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsersWithRoles() {
        try {
            return ResponseEntity.ok(userRepository.findAllWithRoles());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}