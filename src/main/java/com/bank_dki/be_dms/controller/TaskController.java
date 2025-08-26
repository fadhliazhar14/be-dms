package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.TaskDTO;
import com.bank_dki.be_dms.service.TaskService;
import com.bank_dki.be_dms.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks() {
        List<TaskDTO> taskDtos = taskService.getAllTasks();
        ApiResponse<List<TaskDTO>> response = ApiResponse.success("Success", taskDtos);

        return  ResponseEntity.ok(response);
    }
}
