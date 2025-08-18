package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.common.PageRequestDTO;
import com.bank_dki.be_dms.common.PageResponseDTO;
import com.bank_dki.be_dms.dto.CustomerDTO;
import com.bank_dki.be_dms.dto.MessageResponse;
import com.bank_dki.be_dms.service.CustomerService;
import com.bank_dki.be_dms.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<PageResponseDTO<CustomerDTO>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        PageRequestDTO pageRequest = new PageRequestDTO();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSort(sort);
        pageRequest.setDirection(direction);
        pageRequest.setSearch(search);
        pageRequest.setDateFrom(dateFrom);
        pageRequest.setDateTo(dateTo);

        PageResponseDTO<CustomerDTO> customers = customerService.getAllActiveCustomers(pageRequest);
        ApiResponse<PageResponseDTO<CustomerDTO>> response = ApiResponse.success("Success", customers);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerDTO>> getAllCustomersIncludeDeleted() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Short id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/cif/{cifNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<CustomerDTO> getCustomerByCifNumber(@PathVariable String cifNumber) {
        return customerService.getCustomerByCifNumber(cifNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/nik/{nik}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<CustomerDTO> getCustomerByNik(@PathVariable String nik) {
        return customerService.getCustomerByNik(nik)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<MessageResponse> createCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            customerService.createCustomer(customerDTO);
            return ResponseEntity.ok(new MessageResponse("Customer created successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<MessageResponse> updateCustomer(@PathVariable Short id, @RequestBody CustomerDTO customerDTO) {
        try {
            customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(new MessageResponse("Customer updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteCustomer(@PathVariable Short id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(new MessageResponse("Customer deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<List<CustomerDTO>> getCustomersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(customerService.getCustomersByStatus(status));
    }
    
    @GetMapping("/cabang/{cabang}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<List<CustomerDTO>> getCustomersByCabang(@PathVariable String cabang) {
        return ResponseEntity.ok(customerService.getCustomersByCabang(cabang));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<List<CustomerDTO>> searchCustomersByName(@RequestParam String nama) {
        return ResponseEntity.ok(customerService.searchCustomersByName(nama));
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> uploadCsv(@RequestParam("file") MultipartFile file) {
        customerService.saveCustomersFromCsv(file);
        ApiResponse<Void> response = ApiResponse.success("CSV has been uploaded successfully", null);

        return ResponseEntity.ok(response);
    }
}