package com.example.demo.controllers;


import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
       Task task = taskService.findById(id);
       return ResponseEntity.ok(task);
    }

    @PostMapping("")
    @Validated(User.UpdateUser.class)
    public ResponseEntity<Void> create(@Valid @RequestBody Task task) {
        taskService.create(task);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(task.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @Validated(User.UpdateUser.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Task task, @PathVariable Long id) {
        task.setId(id);
        taskService.update(task);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<List<Task>> findAllByUserID(@PathVariable Long userID) {
        List<Task> tasks = taskService.findAllByUserID(userID);
        return ResponseEntity.ok().body(tasks);
    }

}
