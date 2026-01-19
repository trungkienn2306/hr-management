package com.kiennt.hrManagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActiveDepartmentResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long employeeCount;
    private LocalDateTime createdAt;
}