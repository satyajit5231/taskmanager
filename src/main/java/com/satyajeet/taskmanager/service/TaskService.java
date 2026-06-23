package com.satyajeet.taskmanager.service;

import com.satyajeet.taskmanager.dto.Dtos.*;
import com.satyajeet.taskmanager.entity.Task;
import com.satyajeet.taskmanager.entity.Task.TaskStatus;
import com.satyajeet.taskmanager.entity.User;
import com.satyajeet.taskmanager.repository.TaskRepository;
import com.satyajeet.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskResponse createTask(TaskRequest request, String email) {
        User user = getUser(email);
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : Task.TaskPriority.MEDIUM)
                .user(user)
                .build();
        return toResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getTasks(String email, String status) {
        User user = getUser(email);
        List<Task> tasks;
        if (status != null && !status.isBlank()) {
            tasks = taskRepository.findByUserIdAndStatus(user.getId(), TaskStatus.valueOf(status.toUpperCase()));
        } else {
            tasks = taskRepository.findByUserId(user.getId());
        }
        return tasks.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponse updateStatus(Long taskId, StatusUpdateRequest request, String email) {
        User user = getUser(email);
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(request.getStatus());
        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long taskId, String email) {
        User user = getUser(email);
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
