package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.MessageResponse;
import com.bank_dki.be_dms.dto.RoleDTO;
import com.bank_dki.be_dms.entity.Role;
import com.bank_dki.be_dms.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    
    @GetMapping("/active")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<List<Role>> getAllActiveRoles() {
        return ResponseEntity.ok(roleService.getAllActiveRoles());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> getRoleById(@PathVariable Short id) {
        return roleService.getRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> createRole(@RequestBody RoleDTO roleDTO) {
        try {
            roleService.createRole(roleDTO);
            return ResponseEntity.ok(new MessageResponse("Role created successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateRole(@PathVariable Short id, @RequestBody RoleDTO roleDTO) {
        try {
            roleService.updateRole(id, roleDTO);
            return ResponseEntity.ok(new MessageResponse("Role updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteRole(@PathVariable Short id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(new MessageResponse("Role deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> assignRoleToUser(
            @RequestParam Short userId,
            @RequestParam Short roleId) {
        try {
            roleService.assignRoleToUser(userId, roleId);
            return ResponseEntity.ok(new MessageResponse("Role assigned successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> getUserRole(@PathVariable Short userId) {
        try {
            Role role = roleService.getUserRole(userId);
            if (role != null) {
                return ResponseEntity.ok(role);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}