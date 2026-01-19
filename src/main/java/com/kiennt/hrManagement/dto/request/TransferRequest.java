package com.kiennt.hrManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransferRequest {
    @NotNull(message = "To department ID is required")
    private Long toDepartmentId;

    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must be less than 500 characters")
    private String reason;

    @Size(max = 1000, message = "Note must be less than 1000 characters")
    private String note;
}
