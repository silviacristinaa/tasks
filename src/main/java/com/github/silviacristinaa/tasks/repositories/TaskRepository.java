package com.github.silviacristinaa.tasks.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.silviacristinaa.tasks.entities.Task;
import com.github.silviacristinaa.tasks.enums.PriorityEnum;
import com.github.silviacristinaa.tasks.enums.StatusEnum;

public interface TaskRepository extends JpaRepository<Task, Long>{
	
	@Query("SELECT t FROM Task t WHERE (:keyword is null or t.title LIKE %:keyword% or t.description LIKE %:keyword%)"
			+ "and ((:initialDateStartDate is null or :finalDateStartDate is null) or t.startDate BETWEEN "
			+ ":initialDateStartDate AND :finalDateStartDate) "
			+ "and ((:initialDateEndDate is null or :finalDateEndDate is null) or t.endDate BETWEEN "
			+ ":initialDateEndDate AND :finalDateEndDate) "
			+ "and (:priority is null or t.priority = :priority)"
			+ "and (:status is null or t.status = :status)"
			+ "and (:employeeId is null or t.employeeId = :employeeId)")
	List<Task> findByKeywordAndStartDateBetweenAndEndDateBetweenAndPriorityAndStatusAndEmployeeId(
			@Param("keyword") String keyword,
			@Param("initialDateStartDate") LocalDate initialDateStartDate,
			@Param("finalDateStartDate") LocalDate finalDateStartDate, 
			@Param("initialDateEndDate") LocalDate initialDateEndDate,
			@Param("finalDateEndDate") LocalDate finalDateEndDate, 
			@Param("priority") PriorityEnum priority,
			@Param("status") StatusEnum status,
			@Param("employeeId") Long employeeId);
}