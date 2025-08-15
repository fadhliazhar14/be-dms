package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "systemconfigurator")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigurator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SystemConfiguratorId")
    private Short systemConfiguratorId;
    
    @Column(name = "SystemConfiguratorKey", length = 128)
    private String systemConfiguratorKey;
    
    @Column(name = "SystemConfiguratorValue", length = 512)
    private String systemConfiguratorValue;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemConfigurator that = (SystemConfigurator) o;
        return Objects.equals(systemConfiguratorId, that.systemConfiguratorId) && 
               Objects.equals(systemConfiguratorKey, that.systemConfiguratorKey);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(systemConfiguratorId, systemConfiguratorKey);
    }
}