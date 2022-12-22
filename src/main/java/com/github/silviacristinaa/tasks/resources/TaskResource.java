package com.github.silviacristinaa.tasks.resources;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.silviacristinaa.tasks.dtos.requests.TaskRequestDto;
import com.github.silviacristinaa.tasks.dtos.responses.TaskResponseDto;
import com.github.silviacristinaa.tasks.exceptions.BadRequestException;
import com.github.silviacristinaa.tasks.exceptions.InternalServerErrorException;
import com.github.silviacristinaa.tasks.exceptions.NotFoundException;
import com.github.silviacristinaa.tasks.services.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tasks")
public class TaskResource {
	
	private static final String ID = "/{id}";
	
	private final TaskService taskService;

	@GetMapping
	public ResponseEntity<List<TaskResponseDto>> findAll() {
		return ResponseEntity.ok(taskService.findAll());
	}

	@GetMapping(value = ID)
	public ResponseEntity<TaskResponseDto> findById(@PathVariable Long id) throws NotFoundException {
		return ResponseEntity.ok(taskService.findOneTaskById(id));
	}

	@PostMapping
	public ResponseEntity<Void> create(@RequestBody @Valid TaskRequestDto taskRequestDto) throws BadRequestException, NotFoundException, InternalServerErrorException {
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest().path(ID).buildAndExpand(taskService.create(taskRequestDto).getId()).toUri();
		return ResponseEntity.created(uri).build();
	}

	@PutMapping(value = ID)
	public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid TaskRequestDto taskRequestDto) throws NotFoundException, InternalServerErrorException, BadRequestException {
		taskService.update(id, taskRequestDto);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = ID)
	public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException {
		taskService.delete(id);
		return ResponseEntity.noContent().build();
	}
}