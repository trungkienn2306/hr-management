package com.kiennt.hrManagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferHistoryResponse {
    private Long id;
    private EmployeeSimpleResponse employee;
    private DepartmentSimpleResponse fromDepartment;
    private DepartmentSimpleResponse toDepartment;
    private String reason;
    private String note;
    private LocalDateTime transferDate;
}
