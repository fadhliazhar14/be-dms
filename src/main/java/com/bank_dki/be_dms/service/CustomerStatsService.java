package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.CustomerStatusCountDto;
import com.bank_dki.be_dms.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerStatsService {
    
    private final CustomerRepository customerRepository;
    
    public CustomerStatusCountDto getCustomerStatusCountGroupByTasks(String filter) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        switch (filter.toLowerCase()) {
            case "weekly":
                startDate = startDate.with(DayOfWeek.MONDAY);
                endDate = endDate.with(DayOfWeek.SUNDAY);
                break;
            case "monthly" :
                startDate = startDate
                        .with(TemporalAdjusters.firstDayOfMonth())
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = endDate
                        .with(TemporalAdjusters.lastDayOfMonth())
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "yearly" :
                startDate = startDate
                        .with(TemporalAdjusters.firstDayOfYear())
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = endDate
                        .with(TemporalAdjusters.lastDayOfYear())
                        .withHour(0).withMinute(0).withSecond(0).withNano(0);
        }

        List<Object[]> results = customerRepository.countCustomersByStatusBetween(startDate, endDate);
        CustomerStatusCountDto custStatusCount = new CustomerStatusCountDto();
        custStatusCount.setCompleted(231);
        custStatusCount.setTotal(134);
        List<CustomerStatusCountDto.Categories> categories = results.stream()
                .map(result -> new CustomerStatusCountDto.Categories(
                        (String) result[0],
                        (Long) result[1],
                        25.0,
                        "#3B82F6"
                ))
                .toList();
        custStatusCount.setCategories(categories);

        return custStatusCount;
    }
}