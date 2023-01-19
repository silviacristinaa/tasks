package com.github.silviacristinaa.tasks.dtos.requests;

import com.github.silviacristinaa.tasks.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class TaskStatusRequestDto {
	
	private StatusEnum status; 
}