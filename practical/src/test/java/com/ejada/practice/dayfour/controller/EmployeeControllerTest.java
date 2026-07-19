package com.ejada.practice.dayfour.controller;

import com.ejada.practice.dayfour.dto.EmployeeRequest;
import com.ejada.practice.dayfour.dto.EmployeeResponse;
import com.ejada.practice.dayfour.exception.DuplicateResourceException;
import com.ejada.practice.dayfour.exception.GlobalExceptionHandler;
import com.ejada.practice.dayfour.exception.ResourceNotFoundException;
import com.ejada.practice.dayfour.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeController Unit Tests")
/**
 * Controller layer tests for EmployeeController.
 */
public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private com.ejada.practice.dayfour.controller.EmployeeController employeeController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private EmployeeResponse employeeResponse;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        employeeResponse = new EmployeeResponse(
                1L,
                "John Doe",
                "john.doe@ejada.com",
                "Engineering",
                new BigDecimal("15000.00"),
                LocalDateTime.now()
        );

        employeeRequest = new EmployeeRequest(
                "John Doe",
                "john.doe@ejada.com",
                "Engineering",
                new BigDecimal("15000.00")
        );
    }
    /**
     * GET /api/v1/employees.
     */

    @Nested
    @DisplayName("GET /api/v1/employees")
    class GetAllEmployeesTests {

        @Test
        @DisplayName("Should return 200 OK and list of employees")
        void shouldReturnList() throws Exception {
            when(employeeService.getAllEmployees()).thenReturn(List.of(employeeResponse));

            mockMvc.perform(get("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].fullName", is("John Doe")))
                    .andExpect(jsonPath("$[0].email", is("john.doe@ejada.com")));

            verify(employeeService, times(1)).getAllEmployees();
        }

        @Test
        @DisplayName("Should return 200 OK and empty list when no employees exist")
        void shouldReturnEmptyList() throws Exception {
            when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(employeeService, times(1)).getAllEmployees();
        }
    }
    /**
     * GET /api/v1/employees/{id}.
     */

    @Nested
    @DisplayName("GET /api/v1/employees/{id}")
    class GetEmployeeByIdTests {

        @Test
        @DisplayName("Should return 200 OK and employee details when found")
        void shouldReturnEmployee() throws Exception {
            when(employeeService.getEmployeeById(1L)).thenReturn(employeeResponse);

            mockMvc.perform(get("/api/v1/employees/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.fullName", is("John Doe")));

            verify(employeeService, times(1)).getEmployeeById(1L);
        }

        @Test
        @DisplayName("Should return 404 Not Found when employee is not found")
        void shouldReturn404() throws Exception {
            when(employeeService.getEmployeeById(99L))
                    .thenThrow(new ResourceNotFoundException("Employee with id 99 was not found"));

            mockMvc.perform(get("/api/v1/employees/99")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Employee with id 99 was not found")));

            verify(employeeService, times(1)).getEmployeeById(99L);
        }
    }
    /**
     * POST /api/v1/employees.
     */

    @Nested
    @DisplayName("POST /api/v1/employees")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Should return 201 Created and response body when payload is valid")
        void shouldCreateEmployee() throws Exception {
            when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(employeeResponse);

            mockMvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(employeeRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.fullName", is("John Doe")));

            verify(employeeService, times(1)).createEmployee(any(EmployeeRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body lacks fullName")
        void shouldReturn400WhenNameBlank() throws Exception {
            EmployeeRequest invalid = new EmployeeRequest(
                    "",
                    "valid@ejada.com",
                    "Engineering",
                    new BigDecimal("1000")
            );

            mockMvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());

            verify(employeeService, never()).createEmployee(any());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body has invalid email")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            EmployeeRequest invalid = new EmployeeRequest(
                    "John Doe",
                    "invalid-email",
                    "Engineering",
                    new BigDecimal("1000")
            );

            mockMvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());

            verify(employeeService, never()).createEmployee(any());
        }

        @Test
        @DisplayName("Should return 409 Conflict when email is already registered")
        void shouldReturn409WhenDuplicate() throws Exception {
            when(employeeService.createEmployee(any(EmployeeRequest.class)))
                    .thenThrow(new DuplicateResourceException("An employee with email 'john.doe@ejada.com' already exists"));

            mockMvc.perform(post("/api/v1/employees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(employeeRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status", is(409)))
                    .andExpect(jsonPath("$.message", is("An employee with email 'john.doe@ejada.com' already exists")));

            verify(employeeService, times(1)).createEmployee(any(EmployeeRequest.class));
        }
    }
    /**
     * PUT /api/v1/employees/{id}.
     */

    @Nested
    @DisplayName("PUT /api/v1/employees/{id}")
    class UpdateEmployeeTests {

        @Test
        @DisplayName("Should return 200 OK and response body when update is successful")
        void shouldUpdateEmployee() throws Exception {
            when(employeeService.updateEmployee(eq(1L), any(EmployeeRequest.class))).thenReturn(employeeResponse);

            mockMvc.perform(put("/api/v1/employees/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(employeeRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.fullName", is("John Doe")));

            verify(employeeService, times(1)).updateEmployee(eq(1L), any(EmployeeRequest.class));
        }

        @Test
        @DisplayName("Should return 404 Not Found when employee to update does not exist")
        void shouldReturn404() throws Exception {
            when(employeeService.updateEmployee(eq(99L), any(EmployeeRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Employee with id 99 was not found"));

            mockMvc.perform(put("/api/v1/employees/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(employeeRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)));

            verify(employeeService, times(1)).updateEmployee(eq(99L), any(EmployeeRequest.class));
        }
    }
    /**
     * DELETE /api/v1/employees/{id}.
     */

    @Nested
    @DisplayName("DELETE /api/v1/employees/{id}")
    class DeleteEmployeeTests {

        @Test
        @DisplayName("Should return 244 No Content when delete is successful")
        void shouldDeleteEmployee() throws Exception {
            doNothing().when(employeeService).deleteEmployee(1L);

            mockMvc.perform(delete("/api/v1/employees/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(employeeService, times(1)).deleteEmployee(1L);
        }

        @Test
        @DisplayName("Should return 404 Not Found when employee to delete is not found")
        void shouldReturn404() throws Exception {
            doThrow(new ResourceNotFoundException("Employee with id 99 was not found"))
                    .when(employeeService).deleteEmployee(99L);

            mockMvc.perform(delete("/api/v1/employees/99")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)));

            verify(employeeService, times(1)).deleteEmployee(99L);
        }
    }
    /**
     * GET /api/v1/employees/health.
     */

    @Nested
    @DisplayName("GET /api/v1/employees/health")
    class HealthCheckTests {

        @Test
        @DisplayName("Should return 200 OK and text response")
        void shouldReturnHealthStatus() throws Exception {
            mockMvc.perform(get("/api/v1/employees/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Application is running"));
        }
    }
}
