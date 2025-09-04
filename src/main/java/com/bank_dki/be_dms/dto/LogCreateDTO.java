package com.bank_dki.be_dms.dto;

import com.bank_dki.be_dms.model.LogName;
import lombok.Data;

@Data
public class LogCreateDTO {
    private LogName logName;
    private String logNote;
}
