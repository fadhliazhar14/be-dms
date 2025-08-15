package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.RoleDTO;
import com.bank_dki.be_dms.entity.Role;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.repository.RoleRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    public List<Role> getAllActiveRoles() {
        return roleRepository.findAllActiveRoles();
    }
    
    public Optional<Role> getRoleById(Short id) {
        return roleRepository.findById(id);
    }
    
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
    
    public Role createRole(RoleDTO roleDTO) {
        if (roleRepository.existsByRoleName(roleDTO.getRoleName())) {
            throw new RuntimeException("Role already exists: " + roleDTO.getRoleName());
        }
        
        Role role = new Role();
        role.setRoleName(roleDTO.getRoleName());
        role.setRoleIsActive(roleDTO.getRoleIsActive() != null ? roleDTO.getRoleIsActive() : true);
        role.setRoleCreateBy(roleDTO.getRoleCreateBy());
        return roleRepository.save(role);
    }
    
    public Role updateRole(Short id, RoleDTO roleDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        if (!role.getRoleName().equals(roleDTO.getRoleName()) && 
            roleRepository.existsByRoleName(roleDTO.getRoleName())) {
            throw new RuntimeException("Role name already exists: " + roleDTO.getRoleName());
        }
        
        role.setRoleName(roleDTO.getRoleName());
        role.setRoleIsActive(roleDTO.getRoleIsActive());
        role.setRoleUpdateBy(roleDTO.getRoleUpdateBy());
        return roleRepository.save(role);
    }
    
    public void deleteRole(Short id) {
        // Check if any users are assigned to this role
        List<User> users = userRepository.findByRoleId(id);
        if (!users.isEmpty()) {
            throw new RuntimeException("Cannot delete role. There are " + users.size() + " users assigned to this role.");
        }
        roleRepository.deleteById(id);
    }
    
    public User assignRoleToUser(Short userId, Short roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        user.setRoleId(roleId);
        return userRepository.save(user);
    }
    
    public Role getUserRole(Short userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return roleRepository.findById(user.getRoleId())
                .orElse(null);
    }
}