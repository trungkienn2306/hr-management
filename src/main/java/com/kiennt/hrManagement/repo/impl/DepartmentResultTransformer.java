package com.kiennt.hrManagement.repo.impl;

import com.kiennt.hrManagement.dto.response.DepartmentResponse;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DepartmentResultTransformer {

    public DepartmentResponse mapTupleToResponse(Tuple tuple) {
        DepartmentResponse response = new DepartmentResponse();

        try {
            response.setId(tuple.get("id", Long.class));
            response.setCode(tuple.get("code", String.class));
            response.setName(tuple.get("name", String.class));
            response.setDescription(tuple.get("description", String.class));
            response.setStatus(tuple.get("status", Integer.class));
            response.setEmployeeCount(tuple.get("employee_count", Long.class));

            // Map created_at
            Object createdAt = tuple.get("created_at");
            if (createdAt != null) {
                if (createdAt instanceof java.sql.Timestamp) {
                    response.setCreatedAt(((java.sql.Timestamp) createdAt).toLocalDateTime());
                } else if (createdAt instanceof LocalDateTime) {
                    response.setCreatedAt((LocalDateTime) createdAt);
                }
            }

            response.setCreatedBy(tuple.get("created_by", String.class));

        } catch (Exception e) {
            log.error("Error mapping tuple to DepartmentResponse", e);
            throw new RuntimeException("Error mapping query result", e);
        }

        return response;
    }

    public List<DepartmentResponse> mapTuplesToResponses(List<Tuple> tuples) {
        List<DepartmentResponse> responses = new ArrayList<>();
        for (Tuple tuple : tuples) {
            responses.add(mapTupleToResponse(tuple));
        }
        return responses;
    }
}