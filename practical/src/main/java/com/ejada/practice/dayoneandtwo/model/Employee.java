package com.ejada.practice.dayoneandtwo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Plain domain object mapped by hand from JDBC ResultSets (see
 * {@link com.ejada.practice.dayoneandtwo.repository.EmployeeRowMapper}). There is
 * intentionally no JPA/Hibernate here - persistence is done with
 * {@link org.springframework.jdbc.core.JdbcTemplate} so the raw SQL is
 * fully visible and controlled.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private Long id;
    private String fullName;
    private String email;
    private String department;
    private BigDecimal salary;
    private LocalDateTime createdAt;
}
