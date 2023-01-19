package com.github.silviacristinaa.tasks.dtos.responses;

import java.time.LocalDate;

import com.github.silviacristinaa.tasks.enums.PriorityEnum;
import com.github.silviacristinaa.tasks.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class TaskResponseDto {
	
	private Long id; 
	private String title; 
	private String description;
	private LocalDate startDate;    
	private LocalDate endDate; 
	private PriorityEnum priority; 
	private StatusEnum status;  
	private Long employeeId; 
}