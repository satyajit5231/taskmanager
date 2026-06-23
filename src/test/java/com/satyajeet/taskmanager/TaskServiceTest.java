package com.satyajeet.taskmanager;

import com.satyajeet.taskmanager.dto.Dtos.*;
import com.satyajeet.taskmanager.entity.Task;
import com.satyajeet.taskmanager.entity.Task.TaskPriority;
import com.satyajeet.taskmanager.entity.Task.TaskStatus;
import com.satyajeet.taskmanager.entity.User;
import com.satyajeet.taskmanager.repository.TaskRepository;
import com.satyajeet.taskmanager.repository.UserRepository;
import com.satyajeet.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Satyajeet").email("test@test.com").password("pass").build();
        task = Task.builder().id(1L).title("Test Task").description("Desc")
                .status(TaskStatus.PENDING).priority(TaskPriority.MEDIUM).user(user).build();
    }

    @Test
    void createTask_success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskRequest request = new TaskRequest("Test Task", "Desc", TaskPriority.MEDIUM);
        TaskResponse response = taskService.createTask(request, "test@test.com");

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTasks_returnsAllTasksForUser() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByUserId(1L)).thenReturn(List.of(task));

        List<TaskResponse> tasks = taskService.getTasks("test@test.com", null);

        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    void getTasks_filterByStatus() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByUserIdAndStatus(1L, TaskStatus.PENDING)).thenReturn(List.of(task));

        List<TaskResponse> tasks = taskService.getTasks("test@test.com", "PENDING");

        assertEquals(1, tasks.size());
        assertEquals(TaskStatus.PENDING, tasks.get(0).getStatus());
    }

    @Test
    void updateStatus_success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));
        task.setStatus(TaskStatus.COMPLETED);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        StatusUpdateRequest req = new StatusUpdateRequest(TaskStatus.COMPLETED);
        TaskResponse response = taskService.updateStatus(1L, req, "test@test.com");

        assertEquals(TaskStatus.COMPLETED, response.getStatus());
    }

    @Test
    void deleteTask_success() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L, "test@test.com");

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void createTask_userNotFound_throwsException() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                taskService.createTask(new TaskRequest("Task", "Desc", TaskPriority.LOW), "ghost@test.com"));
    }

    @Test
    void deleteTask_notFound_throwsException() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                taskService.deleteTask(99L, "test@test.com"));
    }
}
