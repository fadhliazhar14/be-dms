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
import java.util.ArrayList;
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

        CustomerStatusCountDto custStatusCount = new CustomerStatusCountDto();
        if (isAdmin) {
            Object[] summary = (Object[]) customerRepository.getTotalAndCompleted(true, username, startDate, endDate);
            List<Object[]> results = customerRepository.countCustomersByStatusBetween(true, username, startDate, endDate);
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
        } else {
            List<CustomerStatusCountDto.CategoriesForOperator> rawCategories =
                    customerRepository.getCategoriesForOperator(username, startDate, endDate);

            boolean hasScanning = rawCategories.stream()
                    .anyMatch(c -> "Scanning".equalsIgnoreCase(c.getName()));

            if (!hasScanning) {
                // tambahkan manual jika tidak ada
                rawCategories = new ArrayList<>(rawCategories); // supaya mutable
                rawCategories.add(new CustomerStatusCountDto.CategoriesForOperator("Scanning", 0L));
            }

            long total = rawCategories.stream()
                    .mapToLong(CustomerStatusCountDto.CategoriesForOperator::getCount)
                    .sum();

            long completed = rawCategories.stream()
                    .filter(c -> "Scanning".equalsIgnoreCase(c.getName()))
                    .mapToLong(CustomerStatusCountDto.CategoriesForOperator::getCount)
                    .sum();

            List<CustomerStatusCountDto.Categories> categories = rawCategories.stream()
                    .map(c -> {
                        double percentage = total == 0 ? 0.0
                                : Math.round((c.getCount() * 100.0 / total) * 10.0) / 10.0;

                        String color;
                        switch (c.getName()) {
                            case "Scanning"   -> color = "#3B82F6"; // biru
                            case "Unscanned"  -> color = "#EF4444"; // merah
                            default           -> color = "#9CA3AF"; // abu-abu
                        }

                        return new CustomerStatusCountDto.Categories(
                                c.getName(),
                                c.getCount(),
                                percentage,
                                color
                        );
                    })
                    .toList();

            custStatusCount.setTotal(total);
            custStatusCount.setCompleted(completed);
            custStatusCount.setCategories(categories);
        }

        return custStatusCount;
    }
}