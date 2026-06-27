package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.TaskResponseDTO;
import com.taskflow.taskflow.entity.Task;
import com.taskflow.taskflow.exception.TaskNotFoundException;
import com.taskflow.taskflow.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    // Repository used to perform database operations
    private final TaskRepository taskRepository;

    // Constructor Injection (recommended by Spring)
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Create a new task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Fetch all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Fetch task by ID
    // Throws custom exception if task is not found
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException(
                                "Task not found with ID: " + id));
    }

    // Update an existing task
    public Task updateTask(Long id, Task updatedTask) {

        // Reuse existing method to verify task exists
        Task task = getTaskById(id);

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setPriority(updatedTask.getPriority());
        task.setDueDate(updatedTask.getDueDate());

        return taskRepository.save(task);
    }

    // Delete task after verifying it exists
    public void deleteTask(Long id) {

        Task task = getTaskById(id);

        taskRepository.delete(task);
    }
    /*
     * Convert Entity -> Response DTO
     */
    private TaskResponseDTO mapToDTO(Task task) {

        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());

        return dto;
    }
}