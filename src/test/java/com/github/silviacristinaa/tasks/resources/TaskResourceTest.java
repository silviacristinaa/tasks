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
    private static final LocalDate DATE = LocalDate.now();
    private static final int INDEX = 0;

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
        taskRequestDto = new TaskRequestDto(TITLE, null, DATE, DATE, PriorityEnum.MEDIUM, StatusEnum.COMPLETED, 1l);

        taskStatusRequestDto = new TaskStatusRequestDto(StatusEnum.COMPLETED);

        taskResponseDto = new TaskResponseDto(ID, TITLE, null, DATE, DATE, PriorityEnum.MEDIUM, StatusEnum.COMPLETED, 1l);

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
        assertEquals(DATE, response.getBody().getContent().get(INDEX).getStartDate());
        assertEquals(DATE, response.getBody().getContent().get(INDEX).getEndDate());
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
        assertEquals(DATE, response.getBody().getContent().get(INDEX).getStartDate());
        assertEquals(DATE, response.getBody().getContent().get(INDEX).getEndDate());
        assertEquals(PriorityEnum.MEDIUM, response.getBody().getContent().get(INDEX).getPriority());
        assertEquals(StatusEnum.COMPLETED, response.getBody().getContent().get(INDEX).getStatus());
        assertEquals(ID, response.getBody().getContent().get(INDEX).getEmployeeId());
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
        assertEquals(DATE, response.getBody().getStartDate());
        assertEquals(DATE, response.getBody().getEndDate());
        assertEquals(PriorityEnum.MEDIUM, response.getBody().getPriority());
        assertEquals(StatusEnum.COMPLETED, response.getBody().getStatus());
        assertEquals(ID, response.getBody().getEmployeeId());
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
    void whenUpdateTaskStatusReturnNoContent() throws NotFoundException {
        ResponseEntity<Void> response = taskResource.updateTaskStatus(ID, taskStatusRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
    }

    @Test
    void whenUpdateReturnNoContent() throws NotFoundException, InternalServerErrorException, BadRequestException {
        ResponseEntity<Void> response = taskResource.update(ID, taskRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
    }

    @Test
    void whenDeleteReturnNoContent() throws NotFoundException {
        ResponseEntity<Void> response = taskResource.delete(ID);

        assertNotNull(response);
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService, times(1)).delete(anyLong());
    }
}