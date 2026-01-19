package com.kiennt.hrManagement.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeResponse {
    private Long id;
    private String code;
    private String fullName;
    private String email;
    private String phone;
    private String position;
    private LocalDate dateOfBirth;
    private LocalDate startDate;
    private DepartmentSimpleResponse department;
    private Integer status;
    private LocalDateTime createdAt;
    private String createdBy;
}


