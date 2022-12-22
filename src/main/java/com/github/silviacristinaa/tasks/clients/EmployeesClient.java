package com.github.silviacristinaa.tasks.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.github.silviacristinaa.tasks.dtos.responses.EmployeeResponseDto;

@FeignClient(name = "employees", url = "${integrations.employees.url}")
public interface EmployeesClient {
	
	@GetMapping("/employees/{id}")
	ResponseEntity<EmployeeResponseDto> findById(@PathVariable Long id);
}