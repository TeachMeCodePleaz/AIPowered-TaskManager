package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task mockTask;

    @BeforeEach
    void setUp() {
        mockTask = new Task("Test Task", "Description", LocalDate.now(), Priority.MEDIUM, Status.TODO);
        mockTask.setId(1L);
    }

    @Test
    void createTask_ShouldReturnSavedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        Task created = taskService.createTask(mockTask);

        assertNotNull(created);
        assertEquals("Test Task", created.getTitle());
        verify(taskRepository, times(1)).save(mockTask);
    }

    @Test
    void getAllTasks_ShouldReturnTaskList() {
        when(taskRepository.findAll()).thenReturn(List.of(mockTask));

        List<Task> tasks = taskService.getAllTasks();

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        Optional<Task> foundTask = taskService.getTaskById(1L);

        assertTrue(foundTask.isPresent());
        assertEquals(1L, foundTask.get().getId());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() {
        Task updatedDetails = new Task("Updated Title", "Updated Desc", LocalDate.now(), Priority.HIGH, Status.IN_PROGRESS);
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        Task updated = taskService.updateTask(1L, updatedDetails);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals(Priority.HIGH, updated.getPriority());
        verify(taskRepository, times(1)).save(mockTask);
    }

    @Test
    void deleteTask_ShouldCallRepositoryDelete() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        assertDoesNotThrow(() -> taskService.deleteTask(1L));
        verify(taskRepository, times(1)).deleteById(1L);
    }
}