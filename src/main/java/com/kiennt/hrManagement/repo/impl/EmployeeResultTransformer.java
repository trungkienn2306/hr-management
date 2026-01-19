package com.kiennt.hrManagement.repo.impl;

import com.kiennt.hrManagement.dto.response.EmployeeResponse;
import com.kiennt.hrManagement.dto.response.DepartmentSimpleResponse;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp chuyển đổi dữ liệu (Transformer)
 * Mục đích: Chuyển đổi kết quả truy vấn thô (Native Query Tuple) từ Database
 * sang đối tượng phản hồi (EmployeeResponse DTO)
 */
@Component
@Slf4j
public class EmployeeResultTransformer {

    public EmployeeResponse mapTupleToResponse(Tuple tuple) {
        EmployeeResponse response = new EmployeeResponse();

        try {
            // Map basic employee info
            response.setId(tuple.get("id", Long.class));
            response.setCode(tuple.get("code", String.class));
            response.setFullName(tuple.get("full_name", String.class));
            response.setEmail(tuple.get("email", String.class));
            response.setPhone(tuple.get("phone", String.class));
            response.setPosition(tuple.get("position", String.class));
            response.setStatus(tuple.get("status", Integer.class));

            // Map dates
            Object dateOfBirth = tuple.get("date_of_birth");
            if (dateOfBirth != null) {
                if (dateOfBirth instanceof java.sql.Date) {
                    response.setDateOfBirth(((java.sql.Date) dateOfBirth).toLocalDate());
                } else if (dateOfBirth instanceof LocalDate) {
                    response.setDateOfBirth((LocalDate) dateOfBirth);
                }
            }

            Object startDate = tuple.get("start_date");
            if (startDate != null) {
                if (startDate instanceof java.sql.Date) {
                    response.setStartDate(((java.sql.Date) startDate).toLocalDate());
                } else if (startDate instanceof LocalDate) {
                    response.setStartDate((LocalDate) startDate);
                }
            }

            Object createdAt = tuple.get("created_at");
            if (createdAt != null) {
                if (createdAt instanceof java.sql.Timestamp) {
                    response.setCreatedAt(((java.sql.Timestamp) createdAt).toLocalDateTime());
                } else if (createdAt instanceof LocalDateTime) {
                    response.setCreatedAt((LocalDateTime) createdAt);
                }
            }

            response.setCreatedBy(tuple.get("created_by", String.class));

            // Map department info
            DepartmentSimpleResponse department = new DepartmentSimpleResponse();
            department.setId(tuple.get("department_id", Long.class));
            department.setCode(tuple.get("department_code", String.class));
            department.setName(tuple.get("department_name", String.class));
            response.setDepartment(department);

        } catch (Exception e) {
            log.error("Error mapping tuple to EmployeeResponse", e);
            throw new RuntimeException("Error mapping query result", e);
        }

        return response;
    }

    public List<EmployeeResponse> mapTuplesToResponses(List<Tuple> tuples) {
        List<EmployeeResponse> responses = new ArrayList<>();
        for (Tuple tuple : tuples) {
            responses.add(mapTupleToResponse(tuple));
        }
        return responses;
    }
}