package com.github.silviacristinaa.tasks.dtos.requests;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.github.silviacristinaa.tasks.enums.PriorityEnum;
import com.github.silviacristinaa.tasks.enums.StatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TaskRequestDto {
	 
	@NotBlank 
	private String title; 
	private String description;
	@NotNull
	private LocalDate startDate;  
	@NotNull
	private LocalDate endDate; 
	@NotNull
	private PriorityEnum priority; 
	private StatusEnum status;  
	@NotNull
	private Long employeeId; 
}