package com.ejada.practice.dayoneandtwo.service;

import com.ejada.practice.dayoneandtwo.dto.EmployeeRequest;
import com.ejada.practice.dayoneandtwo.dto.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    void deleteEmployee(Long id);
}
