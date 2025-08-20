package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatusCountDto {
    private Long total;
    private Long completed;
    private List<Categories> categories;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Categories {
        private String name;
        private Long count;
        private double percentage;
        private String color;
    }
}