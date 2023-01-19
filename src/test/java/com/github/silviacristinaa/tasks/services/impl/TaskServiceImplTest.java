package com.github.silviacristinaa.tasks.services.impl;

import com.github.silviacristinaa.tasks.clients.EmployeesClient;
import com.github.silviacristinaa.tasks.dtos.requests.TaskRequestDto;
import com.github.silviacristinaa.tasks.dtos.requests.TaskStatusRequestDto;
import com.github.silviacristinaa.tasks.dtos.responses.EmployeeResponseDto;
import com.github.silviacristinaa.tasks.dtos.responses.TaskResponseDto;
import com.github.silviacristinaa.tasks.entities.Task;
import com.github.silviacristinaa.tasks.enums.DepartmentEnum;
import com.github.silviacristinaa.tasks.enums.PriorityEnum;
import com.github.silviacristinaa.tasks.enums.StatusEnum;
import com.github.silviacristinaa.tasks.exceptions.BadRequestException;
import com.github.silviacristinaa.tasks.exceptions.InternalServerErrorException;
import com.github.silviacristinaa.tasks.exceptions.NotFoundException;
import com.github.silviacristinaa.tasks.repositories.TaskRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TaskServiceImplTest {

    private static final long ID = 1l;
    private static final String TITLE = "Test";
    private static final String DESCRIPTION = "test";
    private static final int INDEX = 0;
    private static final int SIZE = 1;

    private static final String MSG_ERROR_FILLING_DATES = "to perform the search by dates, it is mandatory to fill " +
            "the initial date and final date";
    private static final String MSG_ERROR_DATE_GRATER_THAN = "End date must be greater than start date";
    private static final String TASK_NOT_FOUND = "Task %s not found";
    private static final String EMPLOYEE_NOT_FOUND = "Employee %s not found";
    private static final String EMPLOYEE_IS_INACTIVE = "Employee %s is inactive";
    private static final String PROBLEM_EMPLOYEES_EXTERNAL_API = "There was a problem consuming the employees " +
            "external api";

    private LocalDate localDateStart;
    private LocalDate localDateEnd;
    private TaskRequestDto taskRequestDto;
    private TaskStatusRequestDto taskStatusRequestDto;
    private TaskResponseDto taskResponseDto;
    private EmployeeResponseDto employeeResponseDto;
    private Task task;
    private Request request;

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EmployeesClient employeesClient;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        localDateStart = LocalDate.now();
        localDateEnd = LocalDate.now().plusDays(1);

        taskRequestDto = new TaskRequestDto(TITLE, DESCRIPTION, localDateStart, localDateEnd, PriorityEnum.HIGH,
                StatusEnum.IN_PROGRESS, ID);
        taskStatusRequestDto = new TaskStatusRequestDto(StatusEnum.COMPLETED);

        employeeResponseDto = new EmployeeResponseDto(ID, "Test", "00000000000", DepartmentEnum.IT,
                true);
        taskResponseDto = new TaskResponseDto(ID, TITLE, DESCRIPTION, localDateStart, localDateEnd, PriorityEnum.HIGH,
                StatusEnum.IN_PROGRESS, ID);

        task = new Task(ID, TITLE, DESCRIPTION, localDateStart, localDateEnd, PriorityEnum.HIGH,
                StatusEnum.IN_PROGRESS, ID);

        request = Request.create(Request.HttpMethod.GET, "url", new HashMap<>(), null, new RequestTemplate());
    }

    @Test
    void whenFindAllReturnTaskResponseDtoPage() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(taskResponseDto);

        Page<TaskResponseDto> response = taskServiceImpl.findAll(Pageable.ofSize(SIZE));

        Assertions.assertNotNull(response);
        assertEquals(SIZE, response.getSize());
        assertEquals(TaskResponseDto.class, response.getContent().get(INDEX).getClass());

        assertEquals(TITLE, response.getContent().get(INDEX).getTitle());
        assertEquals(DESCRIPTION, response.getContent().get(INDEX).getDescription());
        assertEquals(localDateStart, response.getContent().get(INDEX).getStartDate());
        assertEquals(localDateEnd, response.getContent().get(INDEX).getEndDate());
        assertEquals(PriorityEnum.HIGH, response.getContent().get(INDEX).getPriority());
        assertEquals(StatusEnum.IN_PROGRESS, response.getContent().get(INDEX).getStatus());
        assertEquals(ID, response.getContent().get(INDEX).getEmployeeId());
    }

    @Test
    void whenFindByFiltersReturnOneTaskResponseDto() throws BadRequestException {
        when(taskRepository.findByKeywordAndStartDateBetweenAndEndDateBetweenAndPriorityAndStatusAndEmployeeId
                (Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                        Mockito.any(), Mockito.any())).thenReturn(List.of(task));
        when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(taskResponseDto);

        Page<TaskResponseDto> response = taskServiceImpl.findByFilters(null, null,
                null, null, null, null, null, null,
                Pageable.ofSize(SIZE));

        assertNotNull(response);
        assertEquals(SIZE, response.getSize());
        assertEquals(TaskResponseDto.class, response.getContent().get(INDEX).getClass());

        assertEquals(TITLE, response.getContent().get(INDEX).getTitle());
        assertEquals(DESCRIPTION, response.getContent().get(INDEX).getDescription());
        assertEquals(localDateStart, response.getContent().get(INDEX).getStartDate());
        assertEquals(localDateEnd, response.getContent().get(INDEX).getEndDate());
        assertEquals(PriorityEnum.HIGH, response.getContent().get(INDEX).getPriority());
        assertEquals(StatusEnum.IN_PROGRESS, response.getContent().get(INDEX).getStatus());
        assertEquals(ID, response.getContent().get(INDEX).getEmployeeId());
    }

    @Test
    void whenTryFindByFiltersWithStartDateNullReturnBadRequestException() {

        BadRequestException exception = assertThrows(BadRequestException.class, () -> taskServiceImpl.findByFilters(
                null, localDateStart, null, null, null, null,
                null, null, Pageable.ofSize(SIZE)));

        assertEquals(MSG_ERROR_FILLING_DATES, exception.getMessage());
    }

    @Test
    void whenTryFindByFiltersWithEndDateNullReturnBadRequestException() {

        BadRequestException exception = assertThrows(BadRequestException.class, () -> taskServiceImpl.findByFilters(
                null, null, null, localDateStart, null, null,
                null, null, Pageable.ofSize(SIZE)));

        assertEquals(MSG_ERROR_FILLING_DATES, exception.getMessage());
    }

    @Test
    void whenTryFindByFiltersWithStartDateGreaterThenReturnBadRequestException() {

        BadRequestException exception = assertThrows(BadRequestException.class, () -> taskServiceImpl.findByFilters(
                null, localDateEnd, localDateStart, null, null, null,
                null, null, Pageable.ofSize(SIZE)));

        assertEquals(MSG_ERROR_DATE_GRATER_THAN, exception.getMessage());
    }

    @Test
    void whenTryFindByFiltersWithEndDateGreaterThenReturnBadRequestException() {

        BadRequestException exception = assertThrows(BadRequestException.class, () -> taskServiceImpl.findByFilters(
                null, null, null, localDateEnd, localDateStart, null,
                null, null, Pageable.ofSize(SIZE)));

        assertEquals(MSG_ERROR_DATE_GRATER_THAN, exception.getMessage());
    }

    @Test
    void whenFindByIdReturnOneTaskResponseDto() throws NotFoundException {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(taskResponseDto);

        TaskResponseDto response = taskServiceImpl.findOneTaskById(ID);

        assertNotNull(response);
        assertEquals(TaskResponseDto.class, response.getClass());

        assertEquals(TITLE, response.getTitle());
        assertEquals(DESCRIPTION, response.getDescription());
        assertEquals(localDateStart, response.getStartDate());
        assertEquals(localDateEnd, response.getEndDate());
        assertEquals(PriorityEnum.HIGH, response.getPriority());
        assertEquals(StatusEnum.IN_PROGRESS, response.getStatus());
        assertEquals(ID, response.getEmployeeId());
    }

    @Test
    void whenTryFindByIdReturnNotFoundException() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> taskServiceImpl.findOneTaskById(ID));

        assertEquals(String.format(TASK_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenCreateReturnSuccess() throws BadRequestException, NotFoundException, InternalServerErrorException {
        when(employeesClient.findById(Mockito.any())).thenReturn(ResponseEntity.ok(employeeResponseDto));
        when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(task);
        when(taskRepository.save(Mockito.any())).thenReturn(task);

        Task response = taskServiceImpl.create(taskRequestDto);

        assertNotNull(response);
        assertEquals(Task.class, response.getClass());

        assertEquals(TITLE, response.getTitle());
        assertEquals(DESCRIPTION, response.getDescription());
        assertEquals(localDateStart, response.getStartDate());
        assertEquals(localDateEnd, response.getEndDate());
        assertEquals(PriorityEnum.HIGH, response.getPriority());
        assertEquals(StatusEnum.IN_PROGRESS, response.getStatus());
        assertEquals(ID, response.getEmployeeId());

        verify(taskRepository, times(1)).save(Mockito.any());
    }

    @Test
    void whenTryCreateWithIncorrectFillingDatesReturnBadRequestException() {
        taskRequestDto.setEndDate(null);
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> taskServiceImpl.create(taskRequestDto));

        assertEquals(MSG_ERROR_FILLING_DATES, exception.getMessage());
    }

    @Test
    void whenTryCreateWithDateGreaterThenReturnBadRequestException() {
        taskRequestDto.setStartDate(localDateEnd);
        taskRequestDto.setEndDate(localDateStart);
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> taskServiceImpl.create(taskRequestDto));

        assertEquals(MSG_ERROR_DATE_GRATER_THAN, exception.getMessage());
    }

    @Test
    void whenTryCreateWithIncorrectEmployeeIdReturnNotFoundException() {
        when(employeesClient.findById(Mockito.anyLong())).thenThrow(new FeignException.NotFound(
                "message", request, null, null));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> taskServiceImpl.create(taskRequestDto));

        assertEquals(String.format(EMPLOYEE_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenTryCreateReturnInternalServerErrorException() {
        when(employeesClient.findById(Mockito.anyLong())).thenThrow(new FeignException.InternalServerError(
                "message", request, null, null));

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class,
                () -> taskServiceImpl.create(taskRequestDto));

        assertEquals(PROBLEM_EMPLOYEES_EXTERNAL_API, exception.getMessage());
    }

    @Test
    void whenTryCreateWithInactiveEmployeeReturnBadRequestException() {
        employeeResponseDto.setEnabled(false);
        when(employeesClient.findById(Mockito.anyLong())).thenReturn(ResponseEntity.ok(employeeResponseDto));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> taskServiceImpl.create(taskRequestDto));

        assertEquals(String.format(EMPLOYEE_IS_INACTIVE, ID), exception.getMessage());
    }

    @Test
    void whenUpdateTaskStatusReturnSuccess() throws NotFoundException {
        when(taskRepository.findById(Mockito.any())).thenReturn(Optional.of(task));

        taskServiceImpl.updateTaskStatus(ID, taskStatusRequestDto);

        verify(taskRepository, times(1)).save(Mockito.any());
    }

    @Test
    void whenTryUpdateTaskStatusReturnNotFoundException() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> taskServiceImpl.updateTaskStatus(ID, taskStatusRequestDto));

        assertEquals(String.format(TASK_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenUpdateReturnSuccess() throws NotFoundException, BadRequestException, InternalServerErrorException {
        when(taskRepository.findById(Mockito.any())).thenReturn(Optional.of(task));
        when(employeesClient.findById(Mockito.any())).thenReturn(ResponseEntity.ok(employeeResponseDto));
        when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(task);

        taskServiceImpl.update(ID, taskRequestDto);

        verify(taskRepository, times(1)).save(Mockito.any());
    }

    @Test
    void whenTryUpdateReturnNotFoundException() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> taskServiceImpl.update(ID, taskRequestDto));

        assertEquals(String.format(TASK_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenTryUpdateWithIncorrectFillingDatesReturnBadRequestException() {
        taskRequestDto.setEndDate(null);
        when(taskRepository.findById(Mockito.any())).thenReturn(Optional.of(task));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> taskServiceImpl.update(ID, taskRequestDto));

        assertEquals(MSG_ERROR_FILLING_DATES, exception.getMessage());
    }

    @Test
    void whenTryUpdateWithDateGreaterReturnBadRequestException() {
        taskRequestDto.setStartDate(localDateEnd);
        taskRequestDto.setEndDate(localDateStart);
        when(taskRepository.findById(Mockito.any())).thenReturn(Optional.of(task));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> taskServiceImpl.update(ID, taskRequestDto));

        assertEquals(MSG_ERROR_DATE_GRATER_THAN, exception.getMessage());
    }

    @Test
    void whenTryUpdateWithIncorrectEmployeeIdReturnNotFoundException() {
        when(taskRepository.findById(Mockito.any())).thenReturn(Optional.of(task));
        when(employeesClient.findById(Mockito.anyLong())).thenThrow(new FeignException.NotFound(
                "message", request, null, null));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> taskServiceImpl.update(ID, taskRequestDto));

        assertEquals(String.format(EMPLOYEE_NOT_FOUND, ID), exception.getMessage());
    }

    @Test
    void whenTryUpdateReturnInternalServerErrorException() {
        when(taskRepository.findById(Mockito.any())).thenReturn(Optional.of(task));
        when(employeesClient.findById(Mockito.anyLong())).thenThrow(new FeignException.InternalServerError(
                "message", request, null, null));

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class,
                () -> taskServiceImpl.update(ID, taskRequestDto));

        assertEquals(PROBLEM_EMPLOYEES_EXTERNAL_API, exception.getMessage());
    }

    @Test
    void whenTryUpdateWithInactiveEmployeeReturnBadRequestException() {
        employeeResponseDto.setEnabled(false);
        when(taskRepository.findById(Mockito.any())).thenReturn(Optional.of(task));
        when(employeesClient.findById(Mockito.anyLong())).thenReturn(ResponseEntity.ok(employeeResponseDto));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> taskServiceImpl.update(ID, taskRequestDto));

        assertEquals(String.format(EMPLOYEE_IS_INACTIVE, ID), exception.getMessage());
    }

    @Test
    void whenDeleteReturnSuccess() throws NotFoundException {
        when(taskRepository.findById(Mockito.any())).thenReturn(Optional.of(task));

        taskServiceImpl.delete(ID);

        verify(taskRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void whenTryDeleteReturnNotFoundException() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> taskServiceImpl.delete(ID));

        assertEquals(String.format(TASK_NOT_FOUND, ID), exception.getMessage());
    }
}