package com.kiennt.hrManagement.repo;

import com.kiennt.hrManagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByCode(String code);

    Optional<Employee> findByEmail(String email);

    boolean existsByCode(String code);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT e FROM Employee e WHERE " +
            "(:departmentId IS NULL OR e.department.id = :departmentId) " +
            "AND (:status IS NULL OR e.status = :status) " +
            "AND (:search IS NULL OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(e.code) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY e.createdAt DESC")
    Page<Employee> searchEmployees(@Param("departmentId") Long departmentId,
                                   @Param("status") Integer status,
                                   @Param("search") String search,
                                   Pageable pageable);
}
