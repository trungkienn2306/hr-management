package com.kiennt.hrManagement.service.impl;

import com.kiennt.hrManagement.dto.request.EmployeeRequest;
import com.kiennt.hrManagement.dto.request.TransferRequest;
import com.kiennt.hrManagement.dto.response.*;
import com.kiennt.hrManagement.entity.Department;
import com.kiennt.hrManagement.entity.Employee;
import com.kiennt.hrManagement.entity.TransferHistory;
import com.kiennt.hrManagement.exception.DuplicateException;
import com.kiennt.hrManagement.exception.ResourceNotFoundException;
import com.kiennt.hrManagement.repo.DepartmentRepository;
import com.kiennt.hrManagement.repo.EmployeeRepository;
import com.kiennt.hrManagement.repo.EmployeeRepositoryCustom;
import com.kiennt.hrManagement.repo.TransferHistoryRepository;
import com.kiennt.hrManagement.service.EmployeeService;
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
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRepositoryCustom employeeRepositoryCustom;
    private final DepartmentRepository departmentRepository;
    private final TransferHistoryRepository transferHistoryRepository;

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        log.info("Creating employee with code: {}", request.getCode());

        // Check duplicate code
        if (employeeRepository.existsByCode(request.getCode())) {
            throw new DuplicateException("Employee code", request.getCode());
        }

        // Check duplicate email
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateException("Employee email", request.getEmail());
        }

        // Get department
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));

        Employee employee = Employee.builder()
                .code(request.getCode())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .position(request.getPosition())
                .dateOfBirth(request.getDateOfBirth())
                .startDate(request.getStartDate())
                .department(department)
                .status(1)
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created successfully with id: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        log.info("Updating employee id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Check if trying to update code
        if (!employee.getCode().equals(request.getCode())) {
            throw new IllegalArgumentException("Cannot change employee code");
        }

        // Check duplicate email
        if (employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new IllegalArgumentException("Employee email already exists: " + request.getEmail());
        }

        // Get new department if changed
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + request.getDepartmentId()));

        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setPosition(request.getPosition());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setStartDate(request.getStartDate());
        employee.setDepartment(department);

        Employee updated = employeeRepository.save(employee);
        log.info("Employee updated successfully: {}", id);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting employee id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        // Soft delete
        employee.setStatus(0);
        employeeRepository.save(employee);
        log.info("Employee soft deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> search(Long departmentId, Integer status, String search, Pageable pageable) {
        log.info("Searching employees - departmentId: {}, status: {}, search: {}",
                departmentId, status, search);

        Page<EmployeeResponse> employees = employeeRepositoryCustom.searchEmployeesDynamic(departmentId, status, search, pageable);

        log.info("Found {} employees", employees.getTotalElements());
        return employees;
    }

    @Override
    @Transactional
    public TransferHistoryResponse transfer(Long employeeId, TransferRequest request) {
        log.info("Transferring employee id: {} to department id: {}", employeeId, request.getToDepartmentId());

        // Get employee with current department
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", employeeId);
                    return new ResourceNotFoundException("Employee not found with id: " + employeeId);
                });

        // Get target department
        Department toDepartment = departmentRepository.findById(request.getToDepartmentId())
                .orElseThrow(() -> {
                    log.error("Target department not found with id: {}", request.getToDepartmentId());
                    return new ResourceNotFoundException("Target department not found with id: " +
                            request.getToDepartmentId());
                });

        // Check if same department
        if (employee.getDepartment().getId().equals(toDepartment.getId())) {
            log.error("Cannot transfer employee {} to the same department {}", employeeId, toDepartment.getId());
            throw new IllegalArgumentException("Cannot transfer to the same department");
        }

        Department fromDepartment = employee.getDepartment();

        // Create transfer history
        TransferHistory transferHistory = TransferHistory.builder()
                .employee(employee)
                .fromDepartment(fromDepartment)
                .toDepartment(toDepartment)
                .reason(request.getReason())
                .note(request.getNote())
                .build();

        // Update employee's department
        employee.setDepartment(toDepartment);

        // Save both in transaction
        employeeRepository.save(employee);
        TransferHistory savedHistory = transferHistoryRepository.save(transferHistory);

        log.info("Employee {} transferred from department {} to {}",
                employeeId, fromDepartment.getId(), toDepartment.getId());

        return mapToTransferResponse(savedHistory);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setCode(employee.getCode());
        response.setFullName(employee.getFullName());
        response.setEmail(employee.getEmail());
        response.setPhone(employee.getPhone());
        response.setPosition(employee.getPosition());
        response.setDateOfBirth(employee.getDateOfBirth());
        response.setStartDate(employee.getStartDate());
        response.setStatus(employee.getStatus());
        response.setCreatedAt(employee.getCreatedAt());
        response.setCreatedBy(employee.getCreatedBy());

        // Map department
        DepartmentSimpleResponse deptResponse = new DepartmentSimpleResponse();
        deptResponse.setId(employee.getDepartment().getId());
        deptResponse.setCode(employee.getDepartment().getCode());
        deptResponse.setName(employee.getDepartment().getName());
        response.setDepartment(deptResponse);

        return response;
    }


    private TransferHistoryResponse mapToTransferResponse(TransferHistory history) {
        TransferHistoryResponse response = new TransferHistoryResponse();
        response.setId(history.getId());
        response.setReason(history.getReason());
        response.setNote(history.getNote());
        response.setTransferDate(history.getTransferDate());

        // Map employee
        EmployeeSimpleResponse empResponse = new EmployeeSimpleResponse();
        empResponse.setId(history.getEmployee().getId());
        empResponse.setCode(history.getEmployee().getCode());
        empResponse.setFullName(history.getEmployee().getFullName());
        response.setEmployee(empResponse);

        // Map from department
        DepartmentSimpleResponse fromDept = new DepartmentSimpleResponse();
        fromDept.setId(history.getFromDepartment().getId());
        fromDept.setCode(history.getFromDepartment().getCode());
        fromDept.setName(history.getFromDepartment().getName());
        response.setFromDepartment(fromDept);

        // Map to department
        DepartmentSimpleResponse toDept = new DepartmentSimpleResponse();
        toDept.setId(history.getToDepartment().getId());
        toDept.setCode(history.getToDepartment().getCode());
        toDept.setName(history.getToDepartment().getName());
        response.setToDepartment(toDept);

        return response;
    }
}
