package com.bank_dki.be_dms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId")
    private Short roleId;
    
    @Column(name = "RoleCreateDate", nullable = true)
    private LocalDateTime roleCreateDate;
    
    @Column(name = "RoleUpdateDate", nullable = true)
    private LocalDateTime roleUpdateDate;
    
    @Column(name = "RoleIsActive", nullable = true)
    private Boolean roleIsActive;
    
    @Column(name = "RoleName", length = 50, nullable = true)
    private String roleName;
    
    @Column(name = "RoleCreateBy", length = 50, nullable = true)
    private String roleCreateBy;
    
    @Column(name = "RoleUpdateBy", length = 50, nullable = true)
    private String roleUpdateBy;
    
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;
    
    @PrePersist
    protected void onCreate() {
        roleCreateDate = LocalDateTime.now();
        roleUpdateDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        roleUpdateDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId) && 
               Objects.equals(roleName, role.roleName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleName);
    }
}