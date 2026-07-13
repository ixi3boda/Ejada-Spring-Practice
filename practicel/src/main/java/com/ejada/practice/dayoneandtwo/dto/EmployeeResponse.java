package com.ejada.practice.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private Long id;

    private String fullName;

    private String email;

    private String department;

    private BigDecimal salary;

    private LocalDateTime createdAt;

}
