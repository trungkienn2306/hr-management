package com.kiennt.hrManagement.repo;

import com.kiennt.hrManagement.dto.response.TransferHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TransferHistoryRepositoryCustom {
    Page<TransferHistoryResponse> searchTransferHistory(
            Long employeeId,
            Long departmentId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);
}