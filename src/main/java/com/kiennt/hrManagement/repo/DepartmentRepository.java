package com.kiennt.hrManagement.repo;

import com.kiennt.hrManagement.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId AND e.status = 1")
    Long countActiveEmployeesByDepartment(@Param("departmentId") Long departmentId);

    @Query("SELECT d FROM Department d WHERE d.status = 1")
    Page<Department> findAllActive(Pageable pageable);

    @Query("SELECT d FROM Department d WHERE d.status = 1 AND " +
            "(d.name LIKE %:search% OR d.code LIKE %:search%)")
    Page<Department> searchActiveDepartments(@Param("search") String search, Pageable pageable);
}
