package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MenuId")
    private Short menuId;
    
    @Column(name = "MenuTitle", length = 40)
    private String menuTitle;
    
    @Column(name = "MenuIsActive")
    private Boolean menuIsActive;
    
    @Column(name = "MenuLink", length = 100)
    private String menuLink;
    
    @Column(name = "MenuFavIcon", length = 100)
    private String menuFavIcon;
    
    @Column(name = "MenuCreateDate")
    private LocalDateTime menuCreateDate;
    
    @Column(name = "MenuUpdateDate")
    private LocalDateTime menuUpdateDate;
    
    @Column(name = "MenuHaveSubItem")
    private Boolean menuHaveSubItem;
    
    @Column(name = "MenuParentId", nullable = false)
    private Short menuParentId;
    
    @Column(name = "MenuCreateBy", length = 50)
    private String menuCreateBy;
    
    @Column(name = "MenuUpdateBy", length = 50)
    private String menuUpdateBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MenuParentId", insertable = false, updatable = false)
    private Menu parentMenu;
    
    @OneToMany(mappedBy = "parentMenu", fetch = FetchType.LAZY)
    private List<Menu> subMenus;
    
    @PrePersist
    protected void onCreate() {
        menuCreateDate = LocalDateTime.now();
        menuUpdateDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        menuUpdateDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return Objects.equals(menuId, menu.menuId) && 
               Objects.equals(menuTitle, menu.menuTitle);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(menuId, menuTitle);
    }
}