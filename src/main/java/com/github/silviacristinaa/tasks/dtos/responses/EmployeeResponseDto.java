package com.github.silviacristinaa.tasks.dtos.responses;

import com.github.silviacristinaa.tasks.enums.DepartmentEnum;

import lombok.Getter;

@Getter
public class EmployeeResponseDto {
	
	private Long id;
	private String name;
	private String cpf; 
	private DepartmentEnum department;
	private boolean enabled;
} 