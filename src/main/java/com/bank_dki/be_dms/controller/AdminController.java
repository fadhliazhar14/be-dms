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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @PostMapping("/assign-admin-role")
    public ResponseEntity<ApiResponse<Void>> assignAdminRole(@RequestParam String email) {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
        
        user.setRoleId(adminRole.getRoleId());
        userRepository.save(user);
        
        ApiResponse<Void> response = ApiResponse.success("ADMIN role assigned to user: " + email, null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all-users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsersForAdmin() {
        List<User> users = userRepository.findAllWithRole();
        ApiResponse<List<User>> response = ApiResponse.success("Success", users);
        return ResponseEntity.ok(response);
    }
}