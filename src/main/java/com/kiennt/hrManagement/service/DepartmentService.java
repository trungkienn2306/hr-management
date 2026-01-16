package com.kiennt.hrManagement.service;

import com.kiennt.hrManagement.dto.request.DepartmentRequest;
import com.kiennt.hrManagement.dto.response.DepartmentResponse;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    DepartmentResponse create(DepartmentRequest request);

    DepartmentResponse update(Long id, DepartmentRequest request);

    void delete(Long id);

    DepartmentResponse getById(Long id);

    Page<DepartmentResponse> getAll(Pageable pageable);

    Page<DepartmentResponse> search(String search, Pageable pageable);
}

