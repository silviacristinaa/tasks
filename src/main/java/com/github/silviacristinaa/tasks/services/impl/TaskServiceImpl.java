package com.github.silviacristinaa.tasks.services.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.silviacristinaa.tasks.clients.EmployeesClient;
import com.github.silviacristinaa.tasks.dtos.requests.TaskRequestDto;
import com.github.silviacristinaa.tasks.dtos.requests.TaskStatusRequestDto;
import com.github.silviacristinaa.tasks.dtos.responses.EmployeeResponseDto;
import com.github.silviacristinaa.tasks.dtos.responses.TaskResponseDto;
import com.github.silviacristinaa.tasks.entities.Task;
import com.github.silviacristinaa.tasks.exceptions.BadRequestException;
import com.github.silviacristinaa.tasks.exceptions.InternalServerErrorException;
import com.github.silviacristinaa.tasks.exceptions.NotFoundException;
import com.github.silviacristinaa.tasks.repositories.TaskRepository;
import com.github.silviacristinaa.tasks.services.TaskService;

import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

	private static final String MSG_ERROR_DATE_GRATER_THAN = "End date must be greater than start date";
	private static final String TASK_NOT_FOUND = "Task %s not found";
	private static final String EMPLOYEE_NOT_FOUND = "Employee %s not found";
	private static final String EMPLOYEE_IS_INACTIVE = "Employee %s is inactive";
	private static final String PROBLEM_EMPLOYEES_EXTERNAL_API = "There was a problem consuming the employees external api";

	private final TaskRepository taskRepository;
	private final ModelMapper modelMapper;
	private final EmployeesClient employeesClient;

	@Override
	public Page<TaskResponseDto> findAll(Pageable pageable) {
		List<TaskResponseDto> response = 
				taskRepository.findAll().stream().map(task -> modelMapper.map(task, TaskResponseDto.class)).collect(Collectors.toList());
		
		final int start = (int)pageable.getOffset();
		final int end = Math.min((start + pageable.getPageSize()), response.size());
		
		Page<TaskResponseDto> page = new PageImpl<>(response.subList(start, end), pageable, response.size());
		return page;
	}

	@Override
	public TaskResponseDto findOneTaskById(Long id) throws NotFoundException {
		Task task = findById(id);
		return modelMapper.map(task, TaskResponseDto.class);
	}

	@Override
	@Transactional
	public Task create(TaskRequestDto taskRequestDto)
			throws BadRequestException, NotFoundException, InternalServerErrorException {
		validateDates(taskRequestDto.getStartDate(), taskRequestDto.getEndDate());
		verifyEmployee(taskRequestDto);

		Task task = modelMapper.map(taskRequestDto, Task.class);
		return taskRepository.save(task);
	}
	
	@Override
	@Transactional
	public void updateTaskStatus(Long id, TaskStatusRequestDto taskStatusRequestDto) throws NotFoundException {
		Task task = findById(id);
		
		task.setStatus(taskStatusRequestDto.getStatus());
		task.setId(id);
		taskRepository.save(task);
	}

	@Override
	@Transactional
	public void update(Long id, TaskRequestDto taskRequestDto)
			throws NotFoundException, BadRequestException, InternalServerErrorException {
		findById(id);
		validateDates(taskRequestDto.getStartDate(), taskRequestDto.getEndDate());
		verifyEmployee(taskRequestDto);

		Task task = modelMapper.map(taskRequestDto, Task.class);
		task.setId(id);
		taskRepository.save(task);
	}

	@Override
	@Transactional
	public void delete(Long id) throws NotFoundException {
		findById(id);
		taskRepository.deleteById(id);
	}

	private Task findById(Long id) throws NotFoundException {
		return taskRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format(TASK_NOT_FOUND, id)));
	}

	private void validateDates(LocalDate startDate, LocalDate endDate) throws BadRequestException {
		if (endDate.isBefore(startDate)) {
			throw new BadRequestException(MSG_ERROR_DATE_GRATER_THAN);
		}
	}

	private void verifyEmployee(TaskRequestDto taskRequestDto)
			throws NotFoundException, InternalServerErrorException, BadRequestException {
		
		EmployeeResponseDto employee = null;
		
		try {
			ResponseEntity<EmployeeResponseDto> employeeResponseDto = employeesClient
					.findById(taskRequestDto.getEmployeeId());

			employee = employeeResponseDto.getBody();
		} catch (FeignClientException ex) {
			if (HttpStatus.NOT_FOUND.value() == ex.status()) {
				throw new NotFoundException(String.format(EMPLOYEE_NOT_FOUND, taskRequestDto.getEmployeeId()));
			}
		} catch (Exception ex) {
			log.error(PROBLEM_EMPLOYEES_EXTERNAL_API, ex);
			throw new InternalServerErrorException(PROBLEM_EMPLOYEES_EXTERNAL_API);
		}
		if (!employee.isEnabled()) {
			throw new BadRequestException(String.format(EMPLOYEE_IS_INACTIVE, taskRequestDto.getEmployeeId()));
		}
	}
}