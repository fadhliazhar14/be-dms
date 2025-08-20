package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.CustomerStatusCountDto;
import com.bank_dki.be_dms.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerStatsService {
    
    private final CustomerRepository customerRepository;
    
    public CustomerStatusCountDto getCustomerStatusCountGroupByTasks() {
        List<Object[]> results = customerRepository.countCustomersByStatus();
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