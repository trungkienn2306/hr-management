package com.kiennt.hrManagement.repo;

import com.kiennt.hrManagement.entity.TransferHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {
    @Query("SELECT th FROM TransferHistory th WHERE th.employee.id = :employeeId " +
            "ORDER BY th.transferDate DESC")
    Page<TransferHistory> findByEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);

    @Query("SELECT th FROM TransferHistory th WHERE " +
            "(:departmentId IS NULL OR th.fromDepartment.id = :departmentId OR th.toDepartment.id = :departmentId) " +
            "ORDER BY th.transferDate DESC")
    Page<TransferHistory> findByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);

}
