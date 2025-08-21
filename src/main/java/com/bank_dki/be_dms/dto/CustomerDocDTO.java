package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDocDTO {
    private String docFilePath;
    private String docFileName;
}
