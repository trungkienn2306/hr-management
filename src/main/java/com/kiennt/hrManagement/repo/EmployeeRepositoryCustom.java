package com.kiennt.hrManagement.repo;

import com.kiennt.hrManagement.dto.response.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepositoryCustom {

    Page<EmployeeResponse> searchEmployeesDynamic(Long departmentId, Integer status, String search, Pageable pageable);
}