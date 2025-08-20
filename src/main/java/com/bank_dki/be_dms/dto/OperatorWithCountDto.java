package com.bank_dki.be_dms.dto;

import com.bank_dki.be_dms.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorWithCountDto {
    private UserDTO operator;
    private Long customerCount;
}
