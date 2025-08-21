package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.CustomerStatusCountDto;
import com.bank_dki.be_dms.repository.CustomerRepository;
import com.bank_dki.be_dms.util.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        LocalDateTime[] dateRange = calculateDateRange(filter);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];

        String username = currentUserUtils.getCurrentUsername();
        boolean isAdmin = currentUserUtils.hasRole("ROLE_ADMIN");

        CustomerStatusCountDto dto = new CustomerStatusCountDto();

        List<CategoryData> categoriesData;
        long total;
        long completed;

        if (isAdmin) {
            Object[] summary = (Object[]) customerRepository.getTotalAndCompleted(true, username, startDate, endDate);
            List<Object[]> results = customerRepository.countCustomersByStatusBetween(true, username, startDate, endDate);

            total = summary[0] != null ? ((Number) summary[0]).longValue() : 0L;
            completed = summary[1] != null ? ((Number) summary[1]).longValue() : 0L;

            categoriesData = results.stream()
                    .map(r -> new CategoryData((String) r[0], r[1] != null ? ((Number) r[1]).longValue() : 0L))
                    .toList();
        } else {
            List<CustomerStatusCountDto.CategoriesForOperator> rawCategories =
                    ensureScanningCategoryExists(customerRepository.getCategoriesForOperator(username, startDate, endDate));

            total = rawCategories.stream().mapToLong(CustomerStatusCountDto.CategoriesForOperator::getCount).sum();
            completed = rawCategories.stream()
                    .filter(c -> "Scanning".equalsIgnoreCase(c.getName()))
                    .mapToLong(CustomerStatusCountDto.CategoriesForOperator::getCount)
                    .sum();

            categoriesData = rawCategories.stream()
                    .map(c -> new CategoryData(c.getName(), c.getCount()))
                    .toList();
        }

        List<CustomerStatusCountDto.Categories> categories = categoriesData.stream()
                .map(cd -> mapToCategory(cd.name, cd.count, isAdmin ? completed : total, getColorForCategory(cd.name)))
                .toList();

        dto.setTotal(total);
        dto.setCompleted(completed);
        dto.setCategories(categories);

        return dto;
    }

    // ----- Helper Classes & Methods -----
    private record CategoryData(String name, long count) {}

    private LocalDateTime[] calculateDateRange(String filter) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now;
        LocalDateTime end = now;

        switch (filter.toLowerCase()) {
            case "weekly" -> {
                start = now.with(DayOfWeek.MONDAY);
                end = now.with(DayOfWeek.SUNDAY);
            }
            case "monthly" -> {
                start = now.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
                end = now.with(TemporalAdjusters.lastDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
            }
            case "yearly" -> {
                start = now.with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
                end = now.with(TemporalAdjusters.lastDayOfYear()).truncatedTo(ChronoUnit.DAYS);
            }
        }
        return new LocalDateTime[]{start, end};
    }

    private List<CustomerStatusCountDto.CategoriesForOperator> ensureScanningCategoryExists(List<CustomerStatusCountDto.CategoriesForOperator> categories) {
        boolean hasScanning = categories.stream().anyMatch(c -> "Scanning".equalsIgnoreCase(c.getName()));
        if (!hasScanning) {
            categories = new ArrayList<>(categories);
            categories.add(new CustomerStatusCountDto.CategoriesForOperator("Scanning", 0L));
        }
        return categories;
    }

    private CustomerStatusCountDto.Categories mapToCategory(String name, long count, long totalForPercentage, String color) {
        double percentage = totalForPercentage == 0 ? 0.0
                : Math.round((count * 100.0 / totalForPercentage) * 10.0) / 10.0;
        return new CustomerStatusCountDto.Categories(name, count, percentage, color);
    }

    private String getColorForCategory(String categoryName) {
        return switch (categoryName) {
            case "Scanning" -> "#3B82F6";
            case "Unscanned" -> "#EF4444";
            default -> "#9CA3AF";
        };
    }

}