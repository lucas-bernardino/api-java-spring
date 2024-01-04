package com.example.demo.services;

import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.services.exceptions.DataBindingViolationException;
import com.example.demo.services.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public Task findById(Long id) {
        Optional<Task> task = taskRepository.findById(id);

        return task.orElseThrow(() -> new ObjectNotFoundException("Nao foi possivel encontrar tarefa com o id:  " + id ));
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
            throw new DataBindingViolationException("Nao foi possivel deletar devido a dependencias de entidades.");
        }
    }

}
