package com.example.demo.repositories;

import com.example.demo.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // essa funcao deve ser escrito nesse formato pois assim o spring entende que quer buscar todas as tasks de um usuario com o seu id.
    List<Task> findTaskByUser_Id(Long id);

}
