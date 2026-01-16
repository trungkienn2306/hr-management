package com.kiennt.hrManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartmentRequest {
    @NotBlank(message = "Department code is required")
    @Size(max = 50, message = "Code must be less than 50 characters")
    private String code;

    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
}