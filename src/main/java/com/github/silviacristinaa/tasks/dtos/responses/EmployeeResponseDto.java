package com.github.silviacristinaa.tasks.dtos.responses;

import com.github.silviacristinaa.tasks.enums.DepartmentEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EmployeeResponseDto {
	
	private Long id;
	private String name;
	private String cpf; 
	private DepartmentEnum department;
	private boolean enabled;
} 