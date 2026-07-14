package com.ejada.practice.dayfour.repository;

import com.ejada.practice.dayfour.model.Employee;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

/**
 * Plain JDBC implementation of {@link EmployeeRepository} using
 * {@link JdbcTemplate}. No JPA/Hibernate involved - every SQL statement is
 * explicit.
 */
@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private static final String SELECT_ALL =
            "SELECT id, full_name, email, department, salary, created_at FROM employees ORDER BY id";

    private static final String SELECT_BY_ID =
            "SELECT id, full_name, email, department, salary, created_at FROM employees WHERE id = ?";

    private static final String EXISTS_BY_EMAIL =
            "SELECT COUNT(*) FROM employees WHERE LOWER(email) = LOWER(?)";

    private static final String INSERT =
            "INSERT INTO employees (full_name, email, department, salary) VALUES (?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE employees SET full_name = ?, email = ?, department = ?, salary = ? WHERE id = ?";

    private static final String DELETE = "DELETE FROM employees WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final EmployeeRowMapper rowMapper = new EmployeeRowMapper();

    public EmployeeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Employee> findAll() {
        return jdbcTemplate.query(SELECT_ALL, rowMapper);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        try {
            Employee employee = jdbcTemplate.queryForObject(SELECT_BY_ID, rowMapper, id);
            return Optional.ofNullable(employee);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_BY_EMAIL, Integer.class, email);
        return count != null && count > 0;
    }


    @Override
    public Employee save(Employee employee) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT, new String[] {"id"});
            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getDepartment());
            ps.setBigDecimal(4, employee.getSalary());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        Long id = generatedId != null ? generatedId.longValue() : null;
        return findById(id)
                .orElseThrow(() -> new IllegalStateException("Failed to load employee immediately after insert"));
    }

    @Override
    public Employee update(Employee employee) {
        jdbcTemplate.update(UPDATE,
                employee.getFullName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getId());
        return findById(employee.getId())
                .orElseThrow(() -> new IllegalStateException("Failed to load employee immediately after update"));
    }

    @Override
    public boolean deleteById(Long id) {
        int rowsAffected = jdbcTemplate.update(DELETE, id);
        return rowsAffected > 0;
    }
}
