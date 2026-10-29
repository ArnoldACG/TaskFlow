package com.arnold.taskflow;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    @GetMapping
    public List<TaskEntity> getAllTasks() {
        return repository.findAll();
    }


    @PostMapping
    public TaskEntity createTask(@Valid @RequestBody TaskEntity task) {
        return repository.save(task);
    }

    @PutMapping("/{id}")
    public TaskEntity updateTask(@PathVariable Long id, @RequestBody TaskEntity taskDetails) {
        return repository.findById(id).map(t -> {
            if (taskDetails.getTitle() != null) t.setTitle(taskDetails.getTitle());
            if (taskDetails.getDescription() != null) t.setDescription(taskDetails.getDescription());
            if (taskDetails.getStatus() != null) t.setStatus(taskDetails.getStatus());
            if (taskDetails.getPriority() != null) t.setPriority(taskDetails.getPriority());
            return repository.save(t);
        }).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        repository.deleteById(id);
    }
}