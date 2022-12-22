package com.github.silviacristinaa.tasks.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.silviacristinaa.tasks.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{

}