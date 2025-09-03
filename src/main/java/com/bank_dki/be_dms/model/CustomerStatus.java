package com.bank_dki.be_dms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerStatus {
    DELIVER("Deliver"),
    REGISTER("Register"),
    SCANNING("Scanning"),
    PENGKINIAN("Pengkinian"),
    PENGKAITAN("Pengkaitan");

    private final String label;
}
