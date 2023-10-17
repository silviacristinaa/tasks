package com.github.silviacristinaa.tasks.resources;

import com.github.silviacristinaa.tasks.dtos.requests.TaskRequestDto;
import com.github.silviacristinaa.tasks.dtos.requests.TaskStatusRequestDto;
import com.github.silviacristinaa.tasks.dtos.responses.TaskResponseDto;
import com.github.silviacristinaa.tasks.entities.Task;
import com.github.silviacristinaa.tasks.enums.PriorityEnum;
import com.github.silviacristinaa.tasks.enums.StatusEnum;
import com.github.silviacristinaa.tasks.exceptions.BadRequestException;
import com.github.silviacristinaa.tasks.exceptions.InternalServerErrorException;
import com.github.silviacristinaa.tasks.exceptions.NotFoundException;
import com.github.silviacristinaa.tasks.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TaskResourceTest {

    private static final long ID = 1l;
    private static final String TITLE = "Test";
    private static final int INDEX = 0;

    private static final String MSG_ERROR_FILLING_DATES = "to perform the search by dates, it is mandatory to fill " +
            "the initial date and final date";
    private static final String TASK_NOT_FOUND = "Task 999 not found";
    private static final String EMPLOYEE_NOT_FOUND = "Employee %s not found";
    private static final String PROBLEM_EMPLOYEES_EXTERNAL_API = "There was a problem consuming the employees " +
            "external api";

    private LocalDate localDateStart;
    private LocalDate localDateEnd;
    private TaskRequestDto taskRequestDto;
    private TaskStatusRequestDto taskStatusRequestDto;
    private TaskResponseDto taskResponseDto;
    private Task task;

    @InjectMocks
    private TaskResource taskResource;

    @Mock
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        localDateStart = LocalDate.now();
        localDateEnd = LocalDate.now().plusDays(1);

        taskRequestDto = new TaskRequestDto(TITLE, null, localDateStart, localDateEnd, PriorityEnum.MEDIUM,
                StatusEnum.COMPLETED, 1l);

        taskStatusRequestDto = new TaskStatusRequestDto(StatusEnum.LATE);

        taskResponseDto = new TaskResponseDto(ID, TITLE, null, localDateStart, localDateEnd, PriorityEnum.MEDIUM,
                StatusEnum.COMPLETED, 1l);

        task = new Task();
    }

    @Test
    void whenFindAllReturnTaskResponseDtoPage() {
        when(taskService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(taskResponseDto)));

        ResponseEntity<Page<TaskResponseDto>> response = taskResource.findAll(Pageable.unpaged());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(TaskResponseDto.class, response.getBody().getContent().get(INDEX).getClass());

        assertEquals(ID, response.getBody().getContent().get(INDEX).getId());
        assertEquals(TITLE, response.getBody().getContent().get(INDEX).getTitle());
        assertNull(response.getBody().getContent().get(INDEX).getDescription());
        assertEquals(localDateStart, response.getBody().getContent().get(INDEX).getStartDate());
        assertEquals(localDateEnd, response.getBody().getContent().get(INDEX).getEndDate());
        assertEquals(PriorityEnum.MEDIUM, response.getBody().getContent().get(INDEX).getPriority());
        assertEquals(StatusEnum.COMPLETED, response.getBody().getContent().get(INDEX).getStatus());
        assertEquals(ID, response.getBody().getContent().get(INDEX).getEmployeeId());
    }

    @Test
    void whenFindByFiltersReturnOneTaskResponseDto() throws BadRequestException {
        when(taskService.findByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(taskResponseDto)));

        ResponseEntity<Page<TaskResponseDto>> response = taskResource.findByFilters(null, null,
                null, null, null, null, null, null, Pageable.unpaged());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(TaskResponseDto.class, response.getBody().getContent().get(INDEX).getClass());

        assertEquals(ID, response.getBody().getContent().get(INDEX).getId());
        assertEquals(TITLE, response.getBody().getContent().get(INDEX).getTitle());
        assertNull(response.getBody().getContent().get(INDEX).getDescription());
        assertEquals(localDateStart, response.getBody().getContent().get(INDEX).getStartDate());
        assertEquals(localDateEnd, response.getBody().getContent().get(INDEX).getEndDate());
        assertEquals(PriorityEnum.MEDIUM, response.getBody().getContent().get(INDEX).getPriority());
        assertEquals(StatusEnum.COMPLETED, response.getBody().getContent().get(INDEX).getStatus());
        assertEquals(ID, response.getBody().getContent().get(INDEX).getEmployeeId());
    }

    @Test
    void whenTryFindByFiltersWithStartDateNullReturnBadRequestException() throws BadRequestException {
        when(taskService.findByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new BadRequestException(MSG_ERROR_FILLING_DATES));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> taskResource
                .findByFilters(null, localDateStart, null, null, null,
                        null, null, null, Pageable.ofSize(1)));

        assertNotNull(exception);
        assertEquals(BadRequestException.class, exception.getClass());
        assertEquals(MSG_ERROR_FILLING_DATES, exception.getMessage());
    }

    @Test
    void whenFindByIdReturnOneTaskResponseDto() throws NotFoundException {
        when(taskService.findOneTaskById(anyLong())).thenReturn(taskResponseDto);

        ResponseEntity<TaskResponseDto> response = taskResource.findById(ID);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(TaskResponseDto.class, response.getBody().getClass());

        assertEquals(ID, response.getBody().getId());
        assertEquals(TITLE, response.getBody().getTitle());
        assertNull(response.getBody().getDescription());
        assertEquals(localDateStart, response.getBody().getStartDate());
        assertEquals(localDateEnd, response.getBody().getEndDate());
        assertEquals(PriorityEnum.MEDIUM, response.getBody().getPriority());
        assertEquals(StatusEnum.COMPLETED, response.getBody().getStatus());
        assertEquals(ID, response.getBody().getEmployeeId());
    }

    @Test
    void whenTryFindByIdReturnNotFoundException() throws NotFoundException {
        when(taskService.findOneTaskById(anyLong())).thenThrow(new NotFoundException(TASK_NOT_FOUND));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> taskResource
                .findById(999L));

        assertNotNull(exception);
        assertEquals(NotFoundException.class, exception.getClass());
        assertEquals(TASK_NOT_FOUND, exception.getMessage());
    }

    @Test
    void whenCreateTaskReturnCreated() throws BadRequestException, NotFoundException, InternalServerErrorException {
        when(taskService.create(any())).thenReturn(task);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<Void> response = taskResource.create(taskRequestDto);

        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Location"));
    }

    @Test
    void whenTryCreateWithIncorrectFillingDatesReturnBadRequestException() throws BadRequestException, NotFoundException, InternalServerErrorException {
        taskRequestDto.setEndDate(null);
        when(taskService.create(any())).thenThrow(new BadRequestException(MSG_ERROR_FILLING_DATES));

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> taskResource.create(taskRequestDto));

        assertNotNull(exception);
        assertEquals(BadRequestException.class, exception.getClass());
        assertEquals(MSG_ERROR_FILLING_DATES, exception.getMessage());
    }

    @Test
    void whenTryCreateWithIncorrectEmployeeIdReturnNotFoundException() throws BadRequestException, NotFoundException, InternalServerErrorException {
        when(taskService.create(any())).thenThrow(new NotFoundException(EMPLOYEE_NOT_FOUND));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> taskResource
                .create(taskRequestDto));

        assertNotNull(exception);
        assertEquals(NotFoundException.class, exception.getClass());
        assertEquals(EMPLOYEE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void whenTryCreateReturnInternalServerErrorException() throws BadRequestException, NotFoundException, InternalServerErrorException {
        when(taskService.create(any())).thenThrow(new InternalServerErrorException(PROBLEM_EMPLOYEES_EXTERNAL_API));

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class, () -> taskResource
                .create(taskRequestDto));

        assertNotNull(exception);
        assertEquals(InternalServerErrorException.class, exception.getClass());
        assertEquals(PROBLEM_EMPLOYEES_EXTERNAL_API, exception.getMessage());
    }

    @Test
    void whenUpdateTaskStatusReturnNoContent() throws NotFoundException {
        ResponseEntity<Void> response = taskResource.updateTaskStatus(ID, taskStatusRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
    }

    @Test
    void whenTryUpdateTaskStatusReturnNotFoundException() throws NotFoundException {
        doThrow(new NotFoundException(TASK_NOT_FOUND)).when(taskService).updateTaskStatus(anyLong(), any());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> taskResource
                .updateTaskStatus(999L, taskStatusRequestDto));

        assertNotNull(exception);
        assertEquals(NotFoundException.class, exception.getClass());
        assertEquals(TASK_NOT_FOUND, exception.getMessage());
    }

    @Test
    void whenUpdateReturnNoContent() throws NotFoundException, InternalServerErrorException, BadRequestException {
        ResponseEntity<Void> response = taskResource.update(ID, taskRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
    }

    @Test
    void whenTryUpdateReturnNotFoundException() throws NotFoundException, BadRequestException, InternalServerErrorException {
        doThrow(new NotFoundException(TASK_NOT_FOUND)).when(taskService).update(anyLong(), any());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> taskResource.update(999L, taskRequestDto));

        assertNotNull(exception);
        assertEquals(NotFoundException.class, exception.getClass());
        assertEquals(TASK_NOT_FOUND, exception.getMessage());
    }

    @Test
    void whenTryUpdateWithIncorrectFillingDatesReturnBadRequestException() throws NotFoundException, BadRequestException, InternalServerErrorException {
        taskRequestDto.setEndDate(null);
        doThrow(new BadRequestException(MSG_ERROR_FILLING_DATES)).when(taskService).update(anyLong(), any());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> taskResource.update(ID, taskRequestDto));

        assertNotNull(exception);
        assertEquals(BadRequestException.class, exception.getClass());
        assertEquals(MSG_ERROR_FILLING_DATES, exception.getMessage());
    }

    @Test
    void whenTryUpdateReturnInternalServerErrorException() throws NotFoundException, BadRequestException, InternalServerErrorException {
        doThrow(new InternalServerErrorException(PROBLEM_EMPLOYEES_EXTERNAL_API)).when(taskService).update(anyLong(), any());

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class, () -> taskResource
                .update(ID, taskRequestDto));

        assertNotNull(exception);
        assertEquals(InternalServerErrorException.class, exception.getClass());
        assertEquals(PROBLEM_EMPLOYEES_EXTERNAL_API, exception.getMessage());
    }

    @Test
    void whenDeleteReturnNoContent() throws NotFoundException {
        ResponseEntity<Void> response = taskResource.delete(ID);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService, times(1)).delete(anyLong());
    }

    @Test
    void whenTryDeleteReturnNotFoundException() throws NotFoundException {
        doThrow(new NotFoundException(TASK_NOT_FOUND)).when(taskService).delete(anyLong());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> taskResource.delete(999L));

        assertNotNull(exception);
        assertEquals(NotFoundException.class, exception.getClass());
        assertEquals(TASK_NOT_FOUND, exception.getMessage());
    }
}