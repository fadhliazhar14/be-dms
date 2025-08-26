package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.entity.Role;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.repository.RoleRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import com.bank_dki.be_dms.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @PostMapping("/make-admin")
    public ResponseEntity<ApiResponse<Void>> makeUserAdmin(@RequestParam String email) {
        User user = userRepository.findByUserNameOrEmailWithRole(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
        
        // Check if user already has admin role
        boolean hasAdminRole = user.getRole() != null && "ADMIN".equals(user.getRole().getRoleName());
        
        if (hasAdminRole) {
            ApiResponse<Void> response = ApiResponse.success("User already has ADMIN role", null);
            return ResponseEntity.ok(response);
        }
        
        user.setRoleId(adminRole.getRoleId());
        userRepository.save(user);
        
        ApiResponse<Void> response = ApiResponse.success("ADMIN role assigned to user: " + email, null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsersWithRoles() {
        List<User> users = userRepository.findAllWithRole();
        ApiResponse<List<User>> response = ApiResponse.success("Success", users);
        return ResponseEntity.ok(response);
    }
}