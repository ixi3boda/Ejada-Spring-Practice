package com.ejada.practice.dayoneandtwo.repository;

import com.ejada.practice.dayoneandtwo.model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Data-access contract for employees. Kept as an interface so the JDBC
 * implementation can be swapped or mocked without touching the service
 * layer.
 */
public interface EmployeeRepository {

    List<Employee> findAll();

    Optional<Employee> findById(Long id);

    boolean existsByEmail(String email);

    Employee save(Employee employee);

    Employee update(Employee employee);

    boolean deleteById(Long id);
}
