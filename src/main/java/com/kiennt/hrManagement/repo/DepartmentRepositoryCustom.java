package com.kiennt.hrManagement.repo;

import com.kiennt.hrManagement.dto.response.DepartmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentRepositoryCustom {
    Page<DepartmentResponse> searchDepartmentsDynamic(String search, Pageable pageable);
}