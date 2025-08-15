package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rolemenu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleMenuId")
    private Short roleMenuId;
    
    @Column(name = "RoleMenuCreateDate")
    private LocalDateTime roleMenuCreateDate;
    
    @Column(name = "RoleMenuUpdateDate")
    private LocalDateTime roleMenuUpdateDate;
    
    @Column(name = "RoleId", nullable = false)
    private Short roleId;
    
    @Column(name = "MenuId", nullable = false)
    private Short menuId;
    
    @Column(name = "RoleMenuName", length = 50)
    private String roleMenuName;
    
    @Column(name = "RoleMenuCreateBy", length = 50)
    private String roleMenuCreateBy;
    
    @Column(name = "RoleMenuUpdateBy", length = 50)
    private String roleMenuUpdateBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoleId", insertable = false, updatable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MenuId", insertable = false, updatable = false)
    private Menu menu;
    
    @PrePersist
    protected void onCreate() {
        roleMenuCreateDate = LocalDateTime.now();
        roleMenuUpdateDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        roleMenuUpdateDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleMenu roleMenu = (RoleMenu) o;
        return Objects.equals(roleMenuId, roleMenu.roleMenuId) && 
               Objects.equals(roleId, roleMenu.roleId) && 
               Objects.equals(menuId, roleMenu.menuId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(roleMenuId, roleId, menuId);
    }
}