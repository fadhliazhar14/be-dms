package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.CustomerStatusCountDto;
import com.bank_dki.be_dms.repository.CustomerRepository;
import com.bank_dki.be_dms.util.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerStatsService {
    private final CustomerRepository customerRepository;
    private final CurrentUserUtils currentUserUtils;
    
    public CustomerStatusCountDto getCustomerStatusCountGroupByTasks(String filter) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        String username = currentUserUtils.getCurrentUsername();
        boolean isAdmin = currentUserUtils.hasRole("ROLE_ADMIN");

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

        Object[] summary = (Object[]) customerRepository.getTotalAndCompleted(isAdmin, username, startDate, endDate);
        List<Object[]> results = customerRepository.countCustomersByStatusBetween(isAdmin, username, startDate, endDate);
        CustomerStatusCountDto custStatusCount = new CustomerStatusCountDto();
        Long total = summary[0] != null ? ((Number) summary[0]).longValue() : 0L;
        Long completed = summary[1] != null ? ((Number) summary[1]).longValue() : 0L;
        custStatusCount.setTotal(total);
        custStatusCount.setCompleted(completed);

        List<CustomerStatusCountDto.Categories> categories = results.stream()
                .map(result -> {
                    String status = (String) result[0];
                    Long count = result[1] != null ? ((Number) result[1]).longValue() : 0L;
                    double percentage = completed == 0 ? 0.0 :  Math.round((count * 100.0 / completed) * 10.0) / 10.0;

                    return new CustomerStatusCountDto.Categories(
                            status,
                            count,
                            percentage,
                            "#3B82F6"
                    );
                })
                .toList();
        custStatusCount.setCategories(categories);

        return custStatusCount;
    }
}