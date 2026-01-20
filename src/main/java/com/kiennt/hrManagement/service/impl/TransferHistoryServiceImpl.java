package com.kiennt.hrManagement.service.impl;

import com.kiennt.hrManagement.dto.response.TransferHistoryResponse;
import com.kiennt.hrManagement.exception.InvalidSearchParameterException;
import com.kiennt.hrManagement.exception.ResourceNotFoundException;
import com.kiennt.hrManagement.repo.TransferHistoryRepository;
import com.kiennt.hrManagement.repo.TransferHistoryRepositoryCustom;
import com.kiennt.hrManagement.service.TransferHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferHistoryServiceImpl implements TransferHistoryService {

    private final TransferHistoryRepository transferHistoryRepository;
    private final TransferHistoryRepositoryCustom transferHistoryRepositoryCustom;

    @Override
    @Transactional(readOnly = true)
    public Page<TransferHistoryResponse> getTransferHistoryByEmployeeId(Long employeeId, Pageable pageable) {
        log.info("Getting transfer history for employee id: {}", employeeId);

        Page<TransferHistoryResponse> history = transferHistoryRepositoryCustom.searchTransferHistory(
                employeeId, null, null, null, pageable);

        if (history.isEmpty()) {
            log.warn("No transfer history found for employee id: {}", employeeId);
        }

        return history;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferHistoryResponse> getTransferHistoryByDepartmentId(Long departmentId, Pageable pageable) {
        log.info("Getting transfer history for department id: {}", departmentId);

        Page<TransferHistoryResponse> history = transferHistoryRepositoryCustom.searchTransferHistory(
                null, departmentId, null, null, pageable);

        if (history.isEmpty()) {
            log.warn("No transfer history found for department id: {}", departmentId);
        }

        return history;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferHistoryResponse> searchTransfers(
            Long employeeId,
            Long departmentId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        log.info("Searching transfer history - employeeId: {}, departmentId: {}, startDate: {}, endDate: {}",
                employeeId, departmentId, startDate, endDate);

        // Validate parameters
        validateSearchParameters(employeeId, departmentId, startDate, endDate);

        Page<TransferHistoryResponse> history = transferHistoryRepositoryCustom.searchTransferHistory(
                employeeId, departmentId, startDate, endDate, pageable);

        log.info("Found {} transfer records for search criteria", history.getTotalElements());
        return history;
    }

    private void validateSearchParameters(Long employeeId, Long departmentId,
                                          LocalDateTime startDate, LocalDateTime endDate) {
        // Validate date range if both are provided (startDate > endDate)
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                log.error("Start date {} is after end date {}", startDate, endDate);
                throw new InvalidSearchParameterException("Start date cannot be after end date");
            }

            // Check if date range is too large (e.g., more than 1 year)
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            if (daysBetween > 365) {
                log.warn("Date range spans {} days, which might be too large for performance", daysBetween);
            }
        }

        if (ObjectUtils.isEmpty(employeeId) && ObjectUtils.isEmpty(departmentId)
                && startDate == null && endDate == null) {
            log.error("At least one search parameter is required");
            throw new InvalidSearchParameterException("At least one search parameter is required");
        }
    }
}