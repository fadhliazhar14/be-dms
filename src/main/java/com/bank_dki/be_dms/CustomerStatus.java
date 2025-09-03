package com.bank_dki.be_dms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum CustomerStatus {
    SCANNING("Scanning"),
    REGISTER("Register"),
    PENGKINIAN("Pengkinian"),
    PENGKAITAN("Pengkaitan");

    private final String label;
}
