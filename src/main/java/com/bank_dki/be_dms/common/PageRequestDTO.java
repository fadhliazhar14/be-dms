package com.bank_dki.be_dms.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PageRequestDTO {
    @Min(value = 0, message = "Page number must be non-negative")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private int size = 20;

    private String sort = "id";

    private String direction = "asc";

    private String search;

    private LocalDate dateFrom;

    private LocalDate dateTo;
}