package com.satyajeet.taskmanager.controller;

import com.satyajeet.taskmanager.dto.Dtos.*;
import com.satyajeet.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TaskResponse task = taskService.createTask(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.<TaskResponse>builder()
                .success(true).message("Task created").data(task).build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<TaskResponse> tasks = taskService.getTasks(userDetails.getUsername(), status);
        return ResponseEntity.ok(ApiResponse.<List<TaskResponse>>builder()
                .success(true).message("Tasks fetched").data(tasks).build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TaskResponse task = taskService.updateStatus(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.<TaskResponse>builder()
                .success(true).message("Status updated").data(task).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        taskService.deleteTask(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Task deleted").build());
    }
}
