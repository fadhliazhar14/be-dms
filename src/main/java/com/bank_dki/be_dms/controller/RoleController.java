package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.RoleDTO;
import com.bank_dki.be_dms.entity.Role;
import com.bank_dki.be_dms.service.RoleService;
import com.bank_dki.be_dms.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        ApiResponse<List<Role>> response = ApiResponse.success("Success", roles);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Role>>> getAllActiveRoles() {
        List<Role> roles = roleService.getAllActiveRoles();
        ApiResponse<List<Role>> response = ApiResponse.success("Success", roles);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable Short id) {
        return roleService.getRoleById(id)
                .map(role -> ResponseEntity.ok(ApiResponse.success("Success", role)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createRole(@RequestBody RoleDTO roleDTO) {
        roleService.createRole(roleDTO);
        ApiResponse<Void> response = ApiResponse.success("Role created successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateRole(@PathVariable Short id, @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(id, roleDTO);
        ApiResponse<Void> response = ApiResponse.success("Role updated successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Short id) {
        roleService.deleteRole(id);
        ApiResponse<Void> response = ApiResponse.success("Role deleted successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignRoleToUser(
            @RequestParam Short userId,
            @RequestParam Short roleId) {
        roleService.assignRoleToUser(userId, roleId);
        ApiResponse<Void> response = ApiResponse.success("Role assigned successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Role>> getUserRole(@PathVariable Short userId) {
        Role role = roleService.getUserRole(userId);
        if (role != null) {
            ApiResponse<Role> response = ApiResponse.success("Success", role);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}