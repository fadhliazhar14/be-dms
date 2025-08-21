package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.CustomerDocDTO;
import com.bank_dki.be_dms.service.CustomerDocService;
import com.bank_dki.be_dms.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/doc")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerDocController {
    private final CustomerDocService customerDocService;

    @GetMapping("/{custId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<CustomerDocDTO>> getCustomerDocByCustId(@PathVariable Short custId) {
        CustomerDocDTO customerDoc = customerDocService.getDocByCustId(custId);
        ApiResponse<CustomerDocDTO> response = ApiResponse.success("Success", customerDoc);

        return  ResponseEntity.ok(response);
    }

    @PatchMapping("/{custId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<CustomerDocDTO>> uploadCustomerDocByCustId(@PathVariable Short custId, @RequestBody CustomerDocDTO request) {
        CustomerDocDTO customerDocUpdated = customerDocService.uploadDocByCustId(custId, request);
        ApiResponse<CustomerDocDTO> response = ApiResponse.success(201, "Customer document has been uploaded successfully", customerDocUpdated);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{custId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomerDocByCustId(@PathVariable Short custId) {
        customerDocService.deleteDocByCustId(custId);
        ApiResponse<Void> response = ApiResponse.success("Customer document has been deleted successfully", null);

        return ResponseEntity.ok(response);
    }
}
