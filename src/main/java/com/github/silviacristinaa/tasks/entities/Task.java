package com.github.silviacristinaa.tasks.entities;

import com.github.silviacristinaa.tasks.enums.PriorityEnum;
import com.github.silviacristinaa.tasks.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
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