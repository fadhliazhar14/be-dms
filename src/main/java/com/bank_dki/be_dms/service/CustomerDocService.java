package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.model.CustomerStatus;
import com.bank_dki.be_dms.dto.CustomerDocDTO;
import com.bank_dki.be_dms.entity.Customer;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.exception.BusinessValidationException;
import com.bank_dki.be_dms.repository.CustomerRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import com.bank_dki.be_dms.util.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomerDocService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CurrentUserUtils currentUserUtils;

    public CustomerDocDTO getDocByCustId(Short custId) {
        return customerRepository.findCustFileByCustId(custId)
                .orElse(new CustomerDocDTO(null, null));
    }

    public CustomerDocDTO uploadDocByCustId(Short custId, CustomerDocDTO customerDocRequest) {
        Customer customer = customerRepository.findById(custId)
                .orElseThrow(() -> new BusinessValidationException("Customer not found"));

        customer.setCustFilePath(customerDocRequest.getDocFilePath());
        customer.setCustFileName(customerDocRequest.getDocFileName());
        customer.setCustStatus(CustomerStatus.SCANNING.getLabel());
        customer.setCustUpdateBy(getFormattedUsername());

        customerRepository.save(customer);

        return new CustomerDocDTO(customerDocRequest.getDocFilePath(), customerDocRequest.getDocFileName());
    }

    public void deleteDocByCustId(Short custId) {
        Customer customer = customerRepository.findById(custId)
                .orElseThrow(() -> new BusinessValidationException("Customer not found"));

        customer.setCustFilePath(null);
        customer.setCustFileName(null);
        customer.setCustStatus(CustomerStatus.REGISTER.getLabel());

        customerRepository.save(customer);
    }

    private String getFormattedUsername () {
        User currentUser = userRepository.findByUserName(currentUserUtils.getCurrentUsername()).orElse(null);
        return currentUser != null ?
                currentUser.getUserName() + " - " + currentUser.getUserJobCode() :
                "null";
    }
}
