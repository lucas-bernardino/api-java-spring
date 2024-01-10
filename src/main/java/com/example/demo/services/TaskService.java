package com.example.demo.services;

import com.example.demo.models.DescriptionRecord;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.auth.TokenService;
import com.example.demo.services.exceptions.DataBindingViolationException;
import com.example.demo.services.exceptions.ObjectNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final TokenService tokenService;

    public TaskService(TaskRepository taskRepository, UserService userService, TokenService tokenService, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    private Long getIdByToken(String token) {
        token = token.replace("Bearer", "").trim();
        String username = tokenService.verifyToken(token);
        Long userId = userRepository.findUserByUsername(username).getId();
        return userId;
    }

    public Task findById(Long id) {
        Optional<Task> task = taskRepository.findById(id);

        return task.orElseThrow(() -> new ObjectNotFoundException("Could not found a task with id " + id ));
    }

    public List<Task> findAllByUserID(Long userID) {
        List<Task> tasks = taskRepository.findTaskByUser_Id(userID);
        return tasks;
    }

    @Transactional
    public Task create(Task task) {
        User verifiyUser = userService.findById(task.getUser().getId());
        task.setId(null);
        task.setUser(verifiyUser);
        task = taskRepository.save(task);
        return task;
    }

    @Transactional
    public Task update(Task task) {
        Task newTask = findById(task.getId());
        task.setDescription(task.getDescription());
        task = taskRepository.save(task);
        return task;
    }

    public void delete(Long id) {
        findById(id);
        try {
            taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Delete was not possible because there are entities dependencies.");
        }
    }

    public List<Task> getAllTasksFromUser(String token) {
        try {
            Long userId = getIdByToken(token);
            List<Task> tasks = findAllByUserID(userId);
            return tasks;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred when trying to get the tasks from the user.", e);
        }
    }

    public void createTaskForUser(String token, DescriptionRecord description) {
        try {
            Long userId = getIdByToken(token);
            User user = userService.findById(userId);
            Task task = new Task(null, description.description(), user);
            taskRepository.save(task);
        } catch(Exception e) {
            throw new RuntimeException("An error occurred when trying to create a task.", e);
        }
    }

    public void deleteTaskFromUser(String token, Long taskId) {
        try {
            Long userId = getIdByToken(token);
            List<Task> tasks = findAllByUserID(userId);
            List<Long> tasksId = tasks.stream().map(task -> task.getId()).toList();
            if (tasksId.contains(taskId)) {
                taskRepository.deleteById(taskId);
                return;
            }
            throw new IllegalArgumentException("ID does not match any of the user tasks");
        } catch(Exception e) {
            throw new RuntimeException("An error occurred when trying to delete a task.", e);
        }
    }

    public void updateTaskFromUser(String token, Long taskId, DescriptionRecord description) {
        try {
            Long userId = getIdByToken(token);
            List<Task> tasks = findAllByUserID(userId);
            List<Long> tasksId = tasks.stream().map(task -> task.getId()).toList();
            if (tasksId.contains(taskId)) {
                Task task = findById(taskId);
                task.setDescription(description.description());
                taskRepository.save(task);
                return;
            }
            throw new IllegalArgumentException("ID does not match any of the user tasks");
        } catch (Exception e) {
            throw new RuntimeException("An error occurred when trying to update a task.", e);
        }


    }
}
