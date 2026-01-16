package com.kiennt.hrManagement.service.impl;

import com.kiennt.hrManagement.dto.request.DepartmentRequest;
import com.kiennt.hrManagement.dto.response.DepartmentResponse;
import com.kiennt.hrManagement.entity.Department;
import com.kiennt.hrManagement.repo.DepartmentRepository;
import com.kiennt.hrManagement.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        log.info("Creating department with code: {}", request.getCode());

        // Check duplicate code
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Department code already exists: " + request.getCode());
        }

        // Check duplicate name
        if (departmentRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Department name already exists: " + request.getName());
        }

        Department department = Department.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .status(1)
                .build();

        Department saved = departmentRepository.save(department);
        log.info("Department created successfully with id: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        log.info("Updating department id: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));

        // Check if trying to update department code
        if (!department.getCode().equals(request.getCode())) {
            throw new IllegalArgumentException("Cannot change department code");
        }

        // Check duplicate department name
        if (departmentRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new IllegalArgumentException("Department name already exists: " + request.getName());
        }

        department.setName(request.getName());
        department.setDescription(request.getDescription());

        Department updated = departmentRepository.save(department);
        log.info("Department updated successfully: {}", id);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting department id: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));

        // Check if department has active employees
        Long employeeCount = departmentRepository.countActiveEmployeesByDepartment(id);
        if (employeeCount > 0) {
            throw new IllegalStateException(
                    String.format("Cannot delete department. There are %d active employees in this department.", employeeCount)
            );
        }

        // Soft delete
        department.setStatus(0);
        departmentRepository.save(department);
        log.info("Department soft deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        return mapToResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAll(Pageable pageable) {
        Page<Department> departments = departmentRepository.findAllActive(pageable);
        return departments.map(this::mapToResponseWithEmployeeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> search(String search, Pageable pageable) {
        Page<Department> departments = departmentRepository.searchActiveDepartments(search, pageable);
        return departments.map(this::mapToResponseWithEmployeeCount);
    }

    private DepartmentResponse mapToResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setCode(department.getCode());
        response.setName(department.getName());
        response.setDescription(department.getDescription());
        response.setStatus(department.getStatus());
        response.setCreatedAt(department.getCreatedAt());
        response.setCreatedBy(department.getCreatedBy());
        return response;
    }

    private DepartmentResponse mapToResponseWithEmployeeCount(Department department) {
        DepartmentResponse response = mapToResponse(department);
        Long employeeCount = departmentRepository.countActiveEmployeesByDepartment(department.getId());
        response.setEmployeeCount(employeeCount);
        return response;
    }
}
