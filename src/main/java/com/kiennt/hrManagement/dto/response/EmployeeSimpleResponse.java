package com.kiennt.hrManagement.dto.response;

import lombok.Data;

@Data
public class EmployeeSimpleResponse {
    private Long id;
    private String code;
    private String fullName;
}
