package com.github.silviacristinaa.tasks.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class GlobalExceptionHandlerTest {

    private static final String EXCEPTION_MSG_BAD_REQUEST = "Bad Request error";
    private static final String EXCEPTION_MSG_UNEXPECTED_ERROR = "Unexpected error";
    private static final String NOT_FOUND_MSG = "Not found";

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void whenBadRequestExceptionReturnResponseEntity() {
        ResponseEntity<ErrorMessage> response = globalExceptionHandler
                .handleMethodBadRequestException(
                        new BadRequestException("End date must be greater than start date"));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(ErrorMessage.class, response.getBody().getClass());
        assertEquals(EXCEPTION_MSG_BAD_REQUEST, response.getBody().getMessage());
        assertEquals("End date must be greater than start date", response.getBody().getErrors().get(0));
    }

    @Test
    void whenInternalServerErrorExceptionReturnResponseEntity() {
        ResponseEntity<ErrorMessage> response = globalExceptionHandler
                .processException(
                        new Exception("There was a problem consuming the employees external api"));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(ErrorMessage.class, response.getBody().getClass());
        assertEquals(EXCEPTION_MSG_UNEXPECTED_ERROR, response.getBody().getMessage());
        assertEquals("There was a problem consuming the employees external api", response.getBody().getErrors().get(0));
    }

    @Test
    void whenNotFoundExceptionReturnResponseEntity() {
        ResponseEntity<ErrorMessage> response = globalExceptionHandler
                .handleMethodArgumentNotFoundException(
                        new NotFoundException(String.format("Task %s not found", 1l)));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(ErrorMessage.class, response.getBody().getClass());
        assertEquals(NOT_FOUND_MSG, response.getBody().getMessage());
        assertEquals("Task 1 not found", response.getBody().getErrors().get(0));
    }
}