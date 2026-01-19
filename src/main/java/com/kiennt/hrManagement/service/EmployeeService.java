package com.kiennt.hrManagement.service;

import com.kiennt.hrManagement.dto.request.DepartmentRequest;
import com.kiennt.hrManagement.dto.request.EmployeeRequest;
import com.kiennt.hrManagement.dto.request.TransferRequest;
import com.kiennt.hrManagement.dto.response.DepartmentResponse;
import com.kiennt.hrManagement.dto.response.EmployeeResponse;
import com.kiennt.hrManagement.dto.response.TransferHistoryResponse;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeResponse create(EmployeeRequest request);

    EmployeeResponse update(Long id, EmployeeRequest request);

    void delete(Long id);

    EmployeeResponse getById(Long id);

    Page<EmployeeResponse> search(Long departmentId, Integer status, String search, Pageable pageable);

    TransferHistoryResponse transfer(Long employeeId, TransferRequest request);
}