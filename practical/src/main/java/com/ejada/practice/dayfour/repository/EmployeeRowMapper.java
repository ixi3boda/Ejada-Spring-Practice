package com.ejada.practice.dayfour.repository;

import com.ejada.practice.dayfour.model.Employee;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Hand-written mapping from a JDBC {@link ResultSet} row to an
 * {@link Employee}. This is the JDBC equivalent of what an ORM would do
 * automatically - kept explicit here since the project intentionally uses
 * plain JDBC instead of JPA/Hibernate.
 */
public class EmployeeRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAt = rs.getTimestamp("created_at");
        return Employee.builder()
                .id(rs.getLong("id"))
                .fullName(rs.getString("full_name"))
                .email(rs.getString("email"))
                .department(rs.getString("department"))
                .salary(rs.getBigDecimal("salary"))
                .createdAt(createdAt != null ? createdAt.toLocalDateTime() : null)
                .build();
    }
}
