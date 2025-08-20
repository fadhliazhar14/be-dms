package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.common.PageRequestDTO;
import com.bank_dki.be_dms.common.PageResponseDTO;
import com.bank_dki.be_dms.dto.UserCreateRequest;
import com.bank_dki.be_dms.dto.UserDTO;
import com.bank_dki.be_dms.dto.UserUpdateRequest;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.exception.ResourceNotFoundException;
import com.bank_dki.be_dms.repository.RoleRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import com.bank_dki.be_dms.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllWithRole().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<UserDTO> getAllActiveUsers() {
        return userRepository.findAllActiveUsers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public UserDTO getUserById(Short id) {
        return convertToDTO(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found")));
    }
    
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByUserEmail(email)
                .map(this::convertToDTO);
    }

    public PageResponseDTO<UserDTO> getAllOperators(PageRequestDTO pageRequest) {
        Pageable pageable = PageUtil.createPageable(pageRequest);

        Page<User> userPage = userRepository.findByRoleIdAndSearch((short) 2, pageRequest.getSearch(), pageable);
        List<UserDTO> userResponse = userPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        return PageUtil.createPageResponse(
                userResponse,
                pageable,
                userPage.getTotalElements()
        );
    }
    
    public User createUser(UserCreateRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByUserEmail(request.getUserEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Validate role exists
        roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        User user = new User();
        user.setUserName(request.getUserName());
        user.setUserEmail(request.getUserEmail());
        user.setUserHashPassword(passwordEncoder.encode(request.getUserHashPassword()));
        user.setRoleId(request.getRoleId());
        user.setUserTglLahir(request.getUserTglLahir());
        user.setUserJabatan(request.getUserJabatan());
        user.setUserTempatLahir(request.getUserTempatLahir());
        user.setUserIsActive(request.getUserIsActive());
        
        return userRepository.save(user);
    }
    
    public User updateUser(Short id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if username is being changed and if it's already taken
        if (!user.getUserName().equals(request.getUserName()) && 
            userRepository.existsByUserName(request.getUserName())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        // Check if email is being changed and if it's already taken
        if (!user.getUserEmail().equals(request.getUserEmail()) && 
            userRepository.existsByUserEmail(request.getUserEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Validate role exists
        roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        user.setUserName(request.getUserName());
        user.setUserEmail(request.getUserEmail());
        user.setRoleId(request.getRoleId());
        user.setUserTglLahir(request.getUserTglLahir());
        user.setUserJabatan(request.getUserJabatan());
        user.setUserTempatLahir(request.getUserTempatLahir());
        user.setUserIsActive(request.getUserIsActive());
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Short id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    public void deactivateUser(Short id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserIsActive(false);
        userRepository.save(user);
    }
    
    public void activateUser(Short id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserIsActive(true);
        userRepository.save(user);
    }
    
    public List<UserDTO> getUsersByRole(Short roleId) {
        return userRepository.findByRoleId(roleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByRoleAndSearch(Short roleId, String search) {
        return userRepository.findByRoleIdAndSearch(roleId, search).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setUserEmail(user.getUserEmail());
        dto.setUserCreateAt(user.getUserCreateAt());
        dto.setUserUpdateAt(user.getUserUpdateAt());
        dto.setUserIsActive(user.getUserIsActive());
        dto.setRoleId(user.getRoleId());
        dto.setUserCreateBy(user.getUserCreateBy());
        dto.setUserUpdateBy(user.getUserUpdateBy());
        dto.setUserTglLahir(user.getUserTglLahir());
        dto.setUserJabatan(user.getUserJabatan());
        dto.setUserTempatLahir(user.getUserTempatLahir());
        
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getRoleName());
        }
        
        return dto;
    }
}