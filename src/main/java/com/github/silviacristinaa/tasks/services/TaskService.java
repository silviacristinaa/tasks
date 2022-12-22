package com.github.silviacristinaa.tasks.services;

import java.util.List;

import com.github.silviacristinaa.tasks.dtos.requests.TaskRequestDto;
import com.github.silviacristinaa.tasks.dtos.responses.TaskResponseDto;
import com.github.silviacristinaa.tasks.entities.Task;
import com.github.silviacristinaa.tasks.exceptions.BadRequestException;
import com.github.silviacristinaa.tasks.exceptions.InternalServerErrorException;
import com.github.silviacristinaa.tasks.exceptions.NotFoundException;

public interface TaskService {
	
	List<TaskResponseDto> findAll();
	
	TaskResponseDto findOneTaskById(Long id) throws NotFoundException;
	
	Task create(TaskRequestDto taskRequestDto) throws BadRequestException, NotFoundException, InternalServerErrorException; 
	
	void update(Long id, TaskRequestDto taskRequestDto) throws NotFoundException, BadRequestException, InternalServerErrorException; 
	
	void delete(Long id) throws NotFoundException;
}