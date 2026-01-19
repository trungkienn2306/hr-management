package com.kiennt.hrManagement.controller;

import com.kiennt.hrManagement.dto.request.DepartmentRequest;
import com.kiennt.hrManagement.dto.request.EmployeeRequest;
import com.kiennt.hrManagement.dto.request.TransferRequest;
import com.kiennt.hrManagement.dto.response.ApiResponse;
import com.kiennt.hrManagement.dto.response.DepartmentResponse;
import com.kiennt.hrManagement.dto.response.EmployeeResponse;
import com.kiennt.hrManagement.dto.response.TransferHistoryResponse;
import com.kiennt.hrManagement.service.DepartmentService;
import com.kiennt.hrManagement.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully"));
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable Long id) {
        EmployeeResponse response = employeeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> search(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmployeeResponse> response = employeeService.search(departmentId, status, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/transfer")
    public ResponseEntity<ApiResponse<TransferHistoryResponse>> transfer(
            @PathVariable Long id,
            @Valid @RequestBody TransferRequest request) {
        TransferHistoryResponse response = employeeService.transfer(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee transferred successfully", response));
    }
}