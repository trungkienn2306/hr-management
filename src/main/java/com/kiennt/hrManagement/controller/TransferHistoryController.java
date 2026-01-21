package com.kiennt.hrManagement.controller;

import com.kiennt.hrManagement.dto.response.ApiResponse;
import com.kiennt.hrManagement.dto.response.TransferHistoryResponse;
import com.kiennt.hrManagement.service.TransferHistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transfer-history")
@RequiredArgsConstructor
@Tag(
        name = "Transfer History API",
        description =  "APIs for viewing transfer history"
)
public class TransferHistoryController {

    private final TransferHistoryService transferHistoryService;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<Page<TransferHistoryResponse>>> getByEmployeeId(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transferDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TransferHistoryResponse> response = transferHistoryService.getTransferHistoryByEmployeeId(employeeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<Page<TransferHistoryResponse>>> getByDepartmentId(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transferDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TransferHistoryResponse> response = transferHistoryService.getTransferHistoryByDepartmentId(departmentId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<TransferHistoryResponse>>> searchTransfers(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transferDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TransferHistoryResponse> response = transferHistoryService.searchTransfers(
                employeeId, departmentId, startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}