package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByRoleName(String roleName);
    Boolean existsByRoleName(String roleName);
    
    @Query("SELECT r FROM Role r WHERE r.roleIsActive = true")
    List<Role> findAllActiveRoles();
}