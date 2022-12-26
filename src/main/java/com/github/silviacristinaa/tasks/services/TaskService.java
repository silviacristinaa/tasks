package com.github.silviacristinaa.tasks.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.silviacristinaa.tasks.dtos.requests.TaskRequestDto;
import com.github.silviacristinaa.tasks.dtos.requests.TaskStatusRequestDto;
import com.github.silviacristinaa.tasks.dtos.responses.TaskResponseDto;
import com.github.silviacristinaa.tasks.entities.Task;
import com.github.silviacristinaa.tasks.exceptions.BadRequestException;
import com.github.silviacristinaa.tasks.exceptions.InternalServerErrorException;
import com.github.silviacristinaa.tasks.exceptions.NotFoundException;

public interface TaskService {
	
	Page<TaskResponseDto> findAll(Pageable pageable);
	
	TaskResponseDto findOneTaskById(Long id) throws NotFoundException;
	
	Task create(TaskRequestDto taskRequestDto) throws BadRequestException, NotFoundException, InternalServerErrorException;  
	
	void updateTaskStatus(Long id, TaskStatusRequestDto taskStatusRequestDto) throws NotFoundException; 
	
	void update(Long id, TaskRequestDto taskRequestDto) throws NotFoundException, BadRequestException, InternalServerErrorException;
	
	void delete(Long id) throws NotFoundException;
}