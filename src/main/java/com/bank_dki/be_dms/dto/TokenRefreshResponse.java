package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshResponse {
    private String accessToken;
    private long accessTokenExpiry;
    private String refreshToken;
    private long refreshTokenExpiry;
}

