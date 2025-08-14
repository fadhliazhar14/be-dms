package com.bank_dki.be_dms.config;

import com.bank_dki.be_dms.entity.Role;
import com.bank_dki.be_dms.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role adminRole;
        Role operatorRole;
        
        if (!roleRepository.existsByName("ADMIN")) {
            adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator role with full access");
            adminRole = roleRepository.save(adminRole);
            log.info("Created ADMIN role");
        } else {
            adminRole = roleRepository.findByName("ADMIN").orElse(null);
        }
        
        if (!roleRepository.existsByName("OPERATOR")) {
            operatorRole = new Role();
            operatorRole.setName("OPERATOR");
            operatorRole.setDescription("Operator role with limited access");
            operatorRole = roleRepository.save(operatorRole);
            log.info("Created OPERATOR role");
        } else {
            operatorRole = roleRepository.findByName("OPERATOR").orElse(null);
        }
        
        // Simple initialization - don't try to auto-assign roles
        log.info("Roles initialized successfully. Use /api/setup/make-admin to assign admin role manually.");
    }
}