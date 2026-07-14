package com.ejada.practice.dayfour.service;

import com.ejada.practice.dayfour.dto.EmployeeRequest;
import com.ejada.practice.dayfour.dto.EmployeeResponse;
import com.ejada.practice.dayfour.exception.DuplicateResourceException;
import com.ejada.practice.dayfour.exception.ResourceNotFoundException;
import com.ejada.practice.dayfour.model.Employee;
import com.ejada.practice.dayfour.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeServiceImpl Unit Tests")
/**
 * Unit tests for EmployeeServiceImpl.
 */
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private com.ejada.practice.dayfour.service.EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequest employeeRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        employee = Employee.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john.doe@ejada.com")
                .department("Engineering")
                .salary(new BigDecimal("15000.00"))
                .createdAt(now)
                .build();

        employeeRequest = new EmployeeRequest(
                "John Doe",
                "john.doe@ejada.com",
                "Engineering",
                new BigDecimal("15000.00")
        );
    }
    /**
     * Get All Employees Tests.
     */

    @Nested
    @DisplayName("Get All Employees Tests")
    class GetAllEmployeesTests {

        @Test
        @DisplayName("Should return list of employee responses when employees exist")
        void shouldReturnEmployeeList() {
            when(employeeRepository.findAll()).thenReturn(List.of(employee));
            List<EmployeeResponse> responses = employeeService.getAllEmployees();
            assertThat(responses).hasSize(1);
            EmployeeResponse response = responses.get(0);
            assertThat(response.getId()).isEqualTo(employee.getId());
            assertThat(response.getFullName()).isEqualTo(employee.getFullName());
            assertThat(response.getEmail()).isEqualTo(employee.getEmail());
            assertThat(response.getDepartment()).isEqualTo(employee.getDepartment());
            assertThat(response.getSalary()).isEqualByComparingTo(employee.getSalary());
            verify(employeeRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no employees exist")
        void shouldReturnEmptyList() {
            when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
            List<EmployeeResponse> responses = employeeService.getAllEmployees();
            assertThat(responses).isEmpty();
            verify(employeeRepository, times(1)).findAll();
        }
    }
    /**
     * Get Employee By ID Tests.
     */

    @Nested
    @DisplayName("Get Employee By ID Tests")
    class GetEmployeeByIdTests {

        @Test
        @DisplayName("Should return employee response when employee exists")
        void shouldReturnEmployeeWhenExists() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            EmployeeResponse response = employeeService.getEmployeeById(1L);
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getFullName()).isEqualTo("John Doe");
            verify(employeeRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when employee does not exist")
        void shouldThrowExceptionWhenNotFound() {
            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Employee with id 99 was not found");
            verify(employeeRepository, times(1)).findById(99L);
        }
    }
    /**
     * Create Employee Tests.
     */

    @Nested
    @DisplayName("Create Employee Tests")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Should successfully create employee when email is unique")
        void shouldCreateEmployeeWhenEmailIsUnique() {
            when(employeeRepository.existsByEmail(employeeRequest.getEmail())).thenReturn(false);
            when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
            EmployeeResponse response = employeeService.createEmployee(employeeRequest);
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo(employeeRequest.getEmail());
            verify(employeeRepository, times(1)).existsByEmail(employeeRequest.getEmail());
            verify(employeeRepository, times(1)).save(any(Employee.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(employeeRepository.existsByEmail(employeeRequest.getEmail())).thenReturn(true);
            assertThatThrownBy(() -> employeeService.createEmployee(employeeRequest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("An employee with email 'john.doe@ejada.com' already exists");
            verify(employeeRepository, times(1)).existsByEmail(employeeRequest.getEmail());
            verify(employeeRepository, never()).save(any(Employee.class));
        }
    }
    /**
     * Update Employee Tests.
     */

    @Nested
    @DisplayName("Update Employee Tests")
    class UpdateEmployeeTests {

        @Test
        @DisplayName("Should successfully update employee when employee exists")
        void shouldUpdateEmployeeWhenExists() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            Employee updatedEmployee = Employee.builder()
                    .id(1L)
                    .fullName("John Doe Updated")
                    .email("john.doe@ejada.com")
                    .department("HR")
                    .salary(new BigDecimal("18000.00"))
                    .createdAt(now)
                    .build();
            when(employeeRepository.update(any(Employee.class))).thenReturn(updatedEmployee);

            EmployeeRequest updateRequest = new EmployeeRequest(
                    "John Doe Updated",
                    "john.doe@ejada.com",
                    "HR",
                    new BigDecimal("18000.00")
            );
            EmployeeResponse response = employeeService.updateEmployee(1L, updateRequest);
            assertThat(response).isNotNull();
            assertThat(response.getFullName()).isEqualTo("John Doe Updated");
            assertThat(response.getDepartment()).isEqualTo("HR");
            assertThat(response.getSalary()).isEqualByComparingTo(new BigDecimal("18000.00"));
            verify(employeeRepository, times(1)).findById(1L);
            verify(employeeRepository, times(1)).update(any(Employee.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when employee to update does not exist")
        void shouldThrowExceptionWhenNotFound() {
            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> employeeService.updateEmployee(99L, employeeRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Employee with id 99 was not found");
            verify(employeeRepository, times(1)).findById(99L);
            verify(employeeRepository, never()).update(any(Employee.class));
        }
    }
    /**
     * Delete Employee Tests.
     */

    @Nested
    @DisplayName("Delete Employee Tests")
    class DeleteEmployeeTests {

        @Test
        @DisplayName("Should successfully delete employee when employee exists")
        void shouldDeleteEmployeeWhenExists() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeRepository.deleteById(1L)).thenReturn(true);
            employeeService.deleteEmployee(1L);
            verify(employeeRepository, times(1)).findById(1L);
            verify(employeeRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when employee to delete does not exist")
        void shouldThrowExceptionWhenNotFound() {
            when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Employee with id 99 was not found");
            verify(employeeRepository, times(1)).findById(99L);
            verify(employeeRepository, never()).deleteById(anyLong());
        }
    }
}
