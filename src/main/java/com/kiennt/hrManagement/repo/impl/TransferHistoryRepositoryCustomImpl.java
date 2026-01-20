package com.kiennt.hrManagement.repo.impl;

import com.kiennt.hrManagement.dto.response.DepartmentSimpleResponse;
import com.kiennt.hrManagement.dto.response.EmployeeSimpleResponse;
import com.kiennt.hrManagement.dto.response.TransferHistoryResponse;
import com.kiennt.hrManagement.repo.TransferHistoryRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@Slf4j
public class TransferHistoryRepositoryCustomImpl implements TransferHistoryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<TransferHistoryResponse> searchTransferHistory(
            Long employeeId,
            Long departmentId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        log.info("Searching transfer history - employeeId: {}, departmentId: {}, startDate: {}, endDate: {}",
                employeeId, departmentId, startDate, endDate);

        StringBuilder baseSql = new StringBuilder();
        baseSql.append("FROM transfer_histories th ");
        baseSql.append("LEFT JOIN employees e ON th.employee_id = e.id ");
        baseSql.append("LEFT JOIN departments fd ON th.from_department_id = fd.id ");
        baseSql.append("LEFT JOIN departments td ON th.to_department_id = td.id ");
        baseSql.append("WHERE 1 = 1 ");

        String selectSql = """
                SELECT 
                    th.id as id,
                    th.reason as reason,
                    th.note as note,
                    th.transfer_date as transfer_date,
                    e.id as employee_id,
                    e.code as employee_code,
                    e.full_name as employee_name,
                    fd.id as from_department_id,
                    fd.code as from_department_code,
                    fd.name as from_department_name,
                    td.id as to_department_id,
                    td.code as to_department_code,
                    td.name as to_department_name
                """;

        // Count SQL
        String countSql = "SELECT COUNT(th.id) ";

        StringBuilder condition = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        List<String> conditions = new ArrayList<>();

        if (!ObjectUtils.isEmpty(employeeId)) {
            conditions.add("th.employee_id = :employeeId");
            parameters.put("employeeId", employeeId);
        }

        if (!ObjectUtils.isEmpty(departmentId)) {
            conditions.add("(th.from_department_id = :departmentId OR th.to_department_id = :departmentId)");
            parameters.put("departmentId", departmentId);
        }

        if (startDate != null) {
            conditions.add("th.transfer_date >= :startDate");
            parameters.put("startDate", startDate);
        }

        if (endDate != null) {
            conditions.add("th.transfer_date <= :endDate");
            parameters.put("endDate", endDate);
        }

        if (!conditions.isEmpty()) {
            condition.append(" AND ").append(String.join(" AND ", conditions));
        }

        // Build order by
        String orderBy = buildOrderBy(pageable);

        // Final SQL queries
        String finalSelectSql = selectSql + baseSql + condition + orderBy;
        String finalCountSql = countSql + baseSql + condition;

        log.debug("Transfer History Search Query: {}", finalSelectSql);
        log.debug("Parameters: {}", parameters);

        Query query = entityManager.createNativeQuery(finalSelectSql, Tuple.class);
        setParameters(query, parameters);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Tuple> tuples = query.getResultList();
        List<TransferHistoryResponse> results = mapTuplesToResponses(tuples);

        // Create query for counting records
        Query countQuery = entityManager.createNativeQuery(finalCountSql);
        setParameters(countQuery, parameters);

        // total records
        Long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(results, pageable, total);
    }

    private List<TransferHistoryResponse> mapTuplesToResponses(List<Tuple> tuples) {
        List<TransferHistoryResponse> responses = new ArrayList<>();
        for (Tuple tuple : tuples) {
            TransferHistoryResponse response = new TransferHistoryResponse();

            response.setId(tuple.get("id", Long.class));
            response.setReason(tuple.get("reason", String.class));
            response.setNote(tuple.get("note", String.class));

            // Map transfer date
            Object transferDate = tuple.get("transfer_date");
            if (transferDate != null) {
                if (transferDate instanceof java.sql.Timestamp) {
                    response.setTransferDate(((java.sql.Timestamp) transferDate).toLocalDateTime());
                } else if (transferDate instanceof LocalDateTime) {
                    response.setTransferDate((LocalDateTime) transferDate);
                }
            }

            // Map employee
            EmployeeSimpleResponse empResponse = new EmployeeSimpleResponse();
            empResponse.setId(tuple.get("employee_id", Long.class));
            empResponse.setCode(tuple.get("employee_code", String.class));
            empResponse.setFullName(tuple.get("employee_name", String.class));
            response.setEmployee(empResponse);

            // Map from department
            DepartmentSimpleResponse fromDept = new DepartmentSimpleResponse();
            fromDept.setId(tuple.get("from_department_id", Long.class));
            fromDept.setCode(tuple.get("from_department_code", String.class));
            fromDept.setName(tuple.get("from_department_name", String.class));
            response.setFromDepartment(fromDept);

            // Map to department
            DepartmentSimpleResponse toDept = new DepartmentSimpleResponse();
            toDept.setId(tuple.get("to_department_id", Long.class));
            toDept.setCode(tuple.get("to_department_code", String.class));
            toDept.setName(tuple.get("to_department_name", String.class));
            response.setToDepartment(toDept);

            responses.add(response);
        }
        return responses;
    }

    private String buildOrderBy(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return " ORDER BY th.transfer_date DESC";
        }

        StringBuilder orderBy = new StringBuilder(" ORDER BY ");
        pageable.getSort().forEach(order -> {
            String property = order.getProperty();
            String columnName = convertPropertyToColumnName(property);
            orderBy.append(columnName)
                    .append(" ")
                    .append(order.getDirection().name())
                    .append(", ");
        });

        // Remove last comma and space
        orderBy.setLength(orderBy.length() - 2);
        return orderBy.toString();
    }

    private String convertPropertyToColumnName(String property) {
        return switch (property) {
            case "id" -> "th.id";
            case "transferDate" -> "th.transfer_date";
            case "employee.fullName" -> "e.full_name";
            case "fromDepartment.name" -> "fd.name";
            case "toDepartment.name" -> "td.name";
            default -> "th." + property;
        };
    }

    private void setParameters(Query query, Map<String, Object> parameters) {
        parameters.forEach(query::setParameter);
    }
}