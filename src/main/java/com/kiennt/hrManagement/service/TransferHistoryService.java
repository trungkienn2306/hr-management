package com.kiennt.hrManagement.service;

import com.kiennt.hrManagement.dto.response.TransferHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TransferHistoryService {

    Page<TransferHistoryResponse> getTransferHistoryByEmployeeId(Long employeeId, Pageable pageable);

    Page<TransferHistoryResponse> getTransferHistoryByDepartmentId(Long departmentId, Pageable pageable);

    Page<TransferHistoryResponse> searchTransfers(
            Long employeeId,
            Long departmentId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);
}