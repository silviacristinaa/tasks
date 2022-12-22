package com.github.silviacristinaa.tasks.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.github.silviacristinaa.tasks.enums.PriorityEnum;
import com.github.silviacristinaa.tasks.enums.StatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	@Column(nullable = false, length = 100)
	private String title; 
	private String description;
	@Column(nullable = false)
	private LocalDate startDate;  
	@Column(nullable = false) 
	private LocalDate endDate; 
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PriorityEnum priority; 
	@Enumerated(EnumType.STRING)
	private StatusEnum status;  
	@Column(nullable = false) 
	private Long employeeId; 
}