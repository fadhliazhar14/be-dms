package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.TaskDTO;
import com.bank_dki.be_dms.entity.Task;
import com.bank_dki.be_dms.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskName(task.getTaskName());
        dto.setTaskCreateDate(task.getTaskCreateDate());
        dto.setTaskCreateBy(task.getTaskCreateBy());
        dto.setTaskUpdateDate(task.getTaskUpdateDate());
        dto.setTaskUpdateBy(task.getTaskUpdateBy());

        return dto;
    }
}
