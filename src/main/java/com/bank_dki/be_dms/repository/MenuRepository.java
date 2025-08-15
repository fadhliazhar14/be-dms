package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Short> {
    @Query("SELECT m FROM Menu m WHERE m.menuIsActive = true")
    List<Menu> findAllActiveMenus();
    
    @Query("SELECT m FROM Menu m WHERE m.menuParentId = :parentId AND m.menuIsActive = true")
    List<Menu> findActiveSubMenusByParentId(@Param("parentId") Short parentId);
    
    @Query("SELECT m FROM Menu m WHERE m.menuParentId = 0 AND m.menuIsActive = true ORDER BY m.menuId")
    List<Menu> findActiveParentMenus();
}