package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.MessageResponse;
import com.bank_dki.be_dms.dto.UserCreateRequest;
import com.bank_dki.be_dms.dto.UserDTO;
import com.bank_dki.be_dms.dto.UserUpdateRequest;
import com.bank_dki.be_dms.service.UserService;
import com.bank_dki.be_dms.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<List<UserDTO>> getAllActiveUsers() {
        return ResponseEntity.ok(userService.getAllActiveUsers());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Short id) {
        UserDTO user = userService.getUserById(id);
        ApiResponse<UserDTO> response = ApiResponse.success("Succes", user);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> createUser(@RequestBody UserCreateRequest request) {
        try {
            userService.createUser(request);
            return ResponseEntity.ok(new MessageResponse("User created successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable Short id, @RequestBody UserUpdateRequest request) {
        try {
            userService.updateUser(id, request);
            return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Short id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deactivateUser(@PathVariable Short id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(new MessageResponse("User deactivated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> activateUser(@PathVariable Short id) {
        try {
            userService.activateUser(id);
            return ResponseEntity.ok(new MessageResponse("User activated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable Short roleId, @RequestParam(required = false) String search) {
        List<UserDTO> users = userService.getUsersByRoleAndSearch(roleId, search);
        ApiResponse<List<UserDTO>> response = ApiResponse.success("Success", users);

        return ResponseEntity.ok(response);
    }
}