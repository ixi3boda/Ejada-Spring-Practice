package com.ejada.practice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payload accepted on create (POST) and update (PUT) requests.
 * Kept separate from {@link com.ejada.practice.model.Employee} so the
 * wire format can evolve independently from the persistence model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "fullName is required")
    @Size(max = 150, message = "fullName must be at most 150 characters")
    private String fullName;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    @Size(max = 150, message = "email must be at most 150 characters")
    private String email;

    @Size(max = 100, message = "department must be at most 100 characters")
    private String department;

    @NotNull(message = "salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "salary must not be negative")
    private BigDecimal salary;

}
