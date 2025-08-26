package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.PageRequestDTO;
import com.bank_dki.be_dms.dto.PageResponseDTO;
import com.bank_dki.be_dms.dto.*;
import com.bank_dki.be_dms.service.UserService;
import com.bank_dki.be_dms.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        ApiResponse<List<UserDTO>> response = ApiResponse.success("Success", users);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllActiveUsers() {
        List<UserDTO> users = userService.getAllActiveUsers();
        ApiResponse<List<UserDTO>> response = ApiResponse.success("Success", users);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Short id) {
        UserDTO user = userService.getUserById(id);
        ApiResponse<UserDTO> response = ApiResponse.success("Succes", user);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(ApiResponse.success("Success", user)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createUser(@Valid @RequestBody UserCreateRequest request) {
        userService.createUser(request);
        ApiResponse<Void> response = ApiResponse.success("User created successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateUser(@PathVariable Short id, @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        ApiResponse<Void> response = ApiResponse.success("User updated successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Short id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = ApiResponse.success("User deleted successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Short id) {
        userService.deactivateUser(id);
        ApiResponse<Void> response = ApiResponse.success("User deactivated successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Short id) {
        userService.activateUser(id);
        ApiResponse<Void> response = ApiResponse.success("User activated successfully!", null);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable Short roleId, @RequestParam(required = false) String search) {
        List<UserDTO> users = userService.getUsersByRoleAndSearch(roleId, search);
        ApiResponse<List<UserDTO>> response = ApiResponse.success("Success", users);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/operators")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<PageResponseDTO<OperatorWithCountDto>>> getOperators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search
    ) {
        PageRequestDTO pageRequest = new PageRequestDTO();
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        pageRequest.setSort(sort);
        pageRequest.setDirection(direction);
        pageRequest.setSearch(search);

        PageResponseDTO<OperatorWithCountDto> operators = userService.getAllOperators(pageRequest);
        ApiResponse<PageResponseDTO<OperatorWithCountDto>> response = ApiResponse.success("Success", operators);

        return ResponseEntity.ok(response);
    }
}