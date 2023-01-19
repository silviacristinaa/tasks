package com.github.silviacristinaa.tasks.dtos.requests;

import com.github.silviacristinaa.tasks.enums.PriorityEnum;
import com.github.silviacristinaa.tasks.enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
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