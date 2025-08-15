package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    private Short menuId;
    private String menuTitle;
    private Boolean menuIsActive;
    private String menuLink;
    private String menuFavIcon;
    private LocalDateTime menuCreateDate;
    private LocalDateTime menuUpdateDate;
    private Boolean menuHaveSubItem;
    private Short menuParentId;
    private String menuCreateBy;
    private String menuUpdateBy;
    private List<MenuDTO> subMenus;
}