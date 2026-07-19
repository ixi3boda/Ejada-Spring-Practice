package com.ejada.practice.daythree.repository;

import com.ejada.practice.daythree.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Employee}. Replaces the previous
 * hand-written {@code EmployeeRepositoryImpl}/{@code EmployeeRowMapper}
 * (plain {@code JdbcTemplate}) implementation - CRUD is now provided
 * automatically by {@link JpaRepository}, and the two custom lookups are
 * derived query methods resolved by Spring Data at startup (no SQL to
 * maintain by hand).
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /** Preserves the {@code ORDER BY id} from the original SELECT_ALL query. */
    List<Employee> findAllByOrderByIdAsc();

    boolean existsByEmailIgnoreCase(String email);
}
