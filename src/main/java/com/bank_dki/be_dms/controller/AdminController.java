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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @PostMapping("/assign-admin-role")
    public ResponseEntity<MessageResponse> assignAdminRole(@RequestParam String email) {
        try {
            User user = userRepository.findByUserEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            user.setRoleId(adminRole.getRoleId());
            userRepository.save(user);
            
            return ResponseEntity.ok(new MessageResponse("ADMIN role assigned to user: " + email));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsersForAdmin() {
        return ResponseEntity.ok(userRepository.findAllWithRole());
    }
}