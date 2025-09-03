package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.PageRequestDTO;
import com.bank_dki.be_dms.dto.PageResponseDTO;
import com.bank_dki.be_dms.dto.CustomerDTO;
import com.bank_dki.be_dms.dto.MessageResponse;
import com.bank_dki.be_dms.service.CustomerService;
import com.bank_dki.be_dms.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Validated
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) {

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
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomersIncludeDeleted() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        ApiResponse<List<CustomerDTO>> response = ApiResponse.success("Success", customers);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerById(@PathVariable Short id) {
        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(ApiResponse.success("Success", customer)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/cif/{cifNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerByCifNumber(@PathVariable String cifNumber) {
        return customerService.getCustomerByCifNumber(cifNumber)
                .map(customer -> ResponseEntity.ok(ApiResponse.success("Success", customer)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/nik/{nik}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerByNik(@PathVariable String nik) {
        return customerService.getCustomerByNik(nik)
                .map(customer -> ResponseEntity.ok(ApiResponse.success("Success", customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account-number/{custNoRek}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerByCustNoRek(@PathVariable String custNoRek) {
        CustomerDTO customer = customerService.getCustomerByCustNoRek(custNoRek);
        ApiResponse<CustomerDTO> response = ApiResponse.success("Success", customer);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        customerService.createCustomer(customerDTO);
        ApiResponse<Void> response = ApiResponse.success("Customer created successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> updateCustomer(@PathVariable Short id, @Valid @RequestBody CustomerDTO customerDTO) {
        customerService.updateCustomer(id, customerDTO);
        ApiResponse<Void> response = ApiResponse.success("Customer updated successfully!", null);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/register/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> registerCustomer(@PathVariable Short id) {
        customerService.registerCustomer(id);
        ApiResponse<Void> response = ApiResponse.success("Customer status has been successfully updated : Register", null);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/pengkinian/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> updateCustomerStatusPengkinian(@PathVariable Short id) {
        customerService.updateCustomerStatusPengkinian(id);
        ApiResponse<Void> response = ApiResponse.success("Customer status has been successfully updated : Pengkinian", null);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/pengkaitan/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> updateCustomerStatusPengkaitan(@PathVariable Short id) {
        customerService.updateCustomerStatusPengkaitan(id);
        ApiResponse<Void> response = ApiResponse.success("Customer status has been successfully updated : Pengkaitan", null);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Short id) {
        customerService.deleteCustomer(id);
        ApiResponse<Void> response = ApiResponse.success("Customer deleted successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getCustomersByStatus(@PathVariable String status) {
        List<CustomerDTO> customers = customerService.getCustomersByStatus(status);
        ApiResponse<List<CustomerDTO>> response = ApiResponse.success("Success", customers);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cabang/{cabang}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getCustomersByCabang(@PathVariable String cabang) {
        List<CustomerDTO> customers = customerService.getCustomersByCabang(cabang);
        ApiResponse<List<CustomerDTO>> response = ApiResponse.success("Success", customers);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> searchCustomersByName(@RequestParam String nama) {
        List<CustomerDTO> customers = customerService.searchCustomersByName(nama);
        ApiResponse<List<CustomerDTO>> response = ApiResponse.success("Success", customers);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> uploadCsv(@RequestParam("file") MultipartFile file) {
        customerService.saveCustomersFromCsv(file);
        ApiResponse<Void> response = ApiResponse.success("CSV has been uploaded successfully", null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<byte[]> downloadCsv(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) {
        PageRequestDTO pageRequest = new PageRequestDTO();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSort(sort);
        pageRequest.setDirection(direction);
        pageRequest.setSearch(search);
        pageRequest.setDateFrom(dateFrom);
        pageRequest.setDateTo(dateTo);

        byte[] customersInCsv = customerService.downloadCustomersToCsv(pageRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(customersInCsv);
    }
}