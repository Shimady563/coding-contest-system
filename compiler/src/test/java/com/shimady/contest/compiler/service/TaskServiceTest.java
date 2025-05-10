package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.exception.ResourceNotFoundException;
import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldGetTaskById() {
        var task = new Task();
        task.setId(1L);
        task.setName("Name");

        given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));

        var foundTask = taskService.getTaskById(task.getId());

        assertEquals(task.getId(), foundTask.getId());
        assertEquals(task.getName(), foundTask.getName());
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundById() {
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTaskById(1L));
    }
}
