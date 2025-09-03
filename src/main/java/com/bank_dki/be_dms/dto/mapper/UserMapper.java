package com.bank_dki.be_dms.dto.mapper;

import com.bank_dki.be_dms.dto.UserDTO;
import com.bank_dki.be_dms.entity.User;

public class UserMapper {

    public static UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setUserEmail(user.getUserEmail());
        dto.setUserJobCode(user.getUserJobCode());
        dto.setUserCreateAt(user.getUserCreateAt());
        dto.setUserUpdateAt(user.getUserUpdateAt());
        dto.setUserIsActive(user.getUserIsActive());
        dto.setRoleId(user.getRoleId());
        dto.setUserCreateBy(user.getUserCreateBy());
        dto.setUserUpdateBy(user.getUserUpdateBy());
        dto.setUserTglLahir(user.getUserTglLahir());
        dto.setUserJabatan(user.getUserJabatan());
        dto.setUserTempatLahir(user.getUserTempatLahir());

        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getRoleName());
        }

        return dto;
    }
}
