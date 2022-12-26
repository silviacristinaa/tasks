package com.github.silviacristinaa.tasks.dtos.requests;

import com.github.silviacristinaa.tasks.enums.StatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TaskStatusRequestDto {
	
	private StatusEnum status; 
}