package com.ejada.practice.service;

import com.ejada.practice.dto.EmployeeRequest;
import com.ejada.practice.dto.EmployeeResponse;
import com.ejada.practice.exception.DuplicateResourceException;
import com.ejada.practice.exception.ResourceNotFoundException;
import com.ejada.practice.model.Employee;
import com.ejada.practice.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        return toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "An employee with email '" + request.getEmail() + "' already exists");
        }

        Employee employee = Employee.builder()
                        .fullName(request.getFullName())
                        .email(request.getEmail())
                        .salary(request.getSalary())
                        .department(request.getDepartment())
                        .build();

        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        findEmployeeOrThrow(id);

        Employee employee = Employee.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .salary(request.getSalary())
                .department(request.getDepartment())
                .build();

        Employee updated = employeeRepository.update(employee);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        findEmployeeOrThrow(id);
        employeeRepository.deleteById(id);
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " was not found"));
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getCreatedAt());
    }
}
