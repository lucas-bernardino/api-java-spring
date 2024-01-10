package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.models.enums.ProfileEnum;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.exceptions.DataBindingViolationException;
import com.example.demo.services.exceptions.ObjectNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException("Nao foi possivel encontrar tarefa com o id:  " + id));
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @Transactional
    public User create(User user) {
        user.setId(null);
        String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user = userRepository.save(user);
        return user;
    }

    @Transactional
    public User update(User user) {
        User newUser = findById(user.getId());
        String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        newUser.setPassword(encryptedPassword);
        return userRepository.save(newUser);
    }

    public void delete(Long id) {
        findById(id);
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Nao foi possivel deletar devido a dependencias de entidades.");
        }
    }



}
