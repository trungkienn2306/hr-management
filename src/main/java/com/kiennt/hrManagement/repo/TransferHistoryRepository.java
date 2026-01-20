package com.kiennt.hrManagement.repo;

import com.kiennt.hrManagement.entity.TransferHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {

    Page<TransferHistory> findByEmployeeId(Long employeeId, Pageable pageable);

    // Tìm lịch sử điều chuyển theo phòng ban (fromDepartment và toDepartment)
    @Query("SELECT th FROM TransferHistory th WHERE " +
            "th.fromDepartment.id = :departmentId OR th.toDepartment.id = :departmentId")
    Page<TransferHistory> findByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);

    Page<TransferHistory> findByEmployeeIdOrderByTransferDateDesc(Long employeeId, Pageable pageable);

    // Tìm lịch sử điều chuyển theo khoảng thời gian
    @Query("SELECT th FROM TransferHistory th WHERE " +
            "th.transferDate BETWEEN :startDate AND :endDate " +
            "ORDER BY th.transferDate DESC")
    Page<TransferHistory> findByTransferDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
