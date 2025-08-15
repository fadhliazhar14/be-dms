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
    
    public List<CustomerStatusCountDto> getCustomerStatusCountGroupByTasks() {
        List<Object[]> results = customerRepository.countCustomersByStatus();
        
        return results.stream()
                .map(result -> new CustomerStatusCountDto(
                        (String) result[0], // custStatus
                        (Long) result[1]    // count
                ))
                .collect(Collectors.toList());
    }
}