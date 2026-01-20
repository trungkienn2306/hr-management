package com.kiennt.hrManagement.repo.impl;

import com.kiennt.hrManagement.dto.response.EmployeeResponse;
import com.kiennt.hrManagement.repo.EmployeeRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private final EmployeeResultTransformer resultTransformer;

    @Override
    public Page<EmployeeResponse> searchEmployeesDynamic(Long departmentId, Integer status, String search, Pageable pageable) {
        log.info("Searching employees with dynamic SQL - departmentId: {}, status: {}, search: {}",
                departmentId, status, search);

        StringBuilder baseSql = new StringBuilder();
        baseSql.append("FROM employees e ");
        baseSql.append("LEFT JOIN departments d ON e.department_id = d.id ");
        baseSql.append("WHERE 1 = 1 ");

        String selectSql = """
                SELECT
                    e.id as id,
                    e.code as code,
                    e.full_name as full_name,
                    e.email as email,
                    e.phone as phone,
                    e.position as position,
                    e.date_of_birth as date_of_birth,
                    e.start_date as start_date,
                    e.department_id as department_id,
                    e.status as status,
                    e.created_at as created_at,
                    e.created_by as created_by,
                    d.code as department_code,
                    d.name as department_name
                """;

        String countSql = "SELECT COUNT(DISTINCT e.id) ";

        StringBuilder condition = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        List<String> conditions = new ArrayList<>();

        if (!ObjectUtils.isEmpty(departmentId)) {
            conditions.add("e.department_id = :departmentId");
            parameters.put("departmentId", departmentId);
        }

        if (Objects.nonNull(status)) {
            conditions.add("e.status = :status");
            parameters.put("status", status);
        } else {
            conditions.add("e.status = 1");
        }

        if (StringUtils.hasText(search)) {
            conditions.add("""
                (LOWER(e.full_name) LIKE LOWER(CONCAT('%', :search, '%')) 
                 OR LOWER(e.code) LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(e.phone) LIKE LOWER(CONCAT('%', :search, '%')))
                """);
            parameters.put("search", search);
        }

        conditions.add("d.status = 1");

        condition.append(" AND ").append(String.join(" AND ", conditions));

        String orderBy = buildOrderBy(pageable);

        String finalSelectSql = selectSql + baseSql + condition + orderBy;
        String finalCountSql = countSql + baseSql + condition;

        log.debug("Employee Search Query: {}", finalSelectSql);
        log.debug("Parameters: {}", parameters);

        // Create query for data (Tuple)
        Query query = entityManager.createNativeQuery(finalSelectSql, Tuple.class);
        setParameters(query, parameters);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Map tuples to dto
        List<Tuple> tuples = query.getResultList();
        List<EmployeeResponse> results = resultTransformer.mapTuplesToResponses(tuples);

        // Create query for count
        Query countQuery = entityManager.createNativeQuery(finalCountSql);
        setParameters(countQuery, parameters);

        Long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(results, pageable, total);
    }


    private String buildOrderBy(Pageable pageable) {
        StringBuilder orderBy = new StringBuilder(" ORDER BY ");

        if (pageable.getSort().isEmpty()) {
            orderBy.append("e.created_at DESC");
        } else {
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
        }
        orderBy.append(", e.id DESC");
        return orderBy.toString();
    }

    private String convertPropertyToColumnName(String property) {
        // Map entity property names to database column names
        return switch (property) {
            case "id" -> "e.id";
            case "code" -> "e.code";
            case "fullName" -> "e.full_name";
            case "email" -> "e.email";
            case "phone" -> "e.phone";
            case "position" -> "e.position";
            case "dateOfBirth" -> "e.date_of_birth";
            case "startDate" -> "e.start_date";
            case "createdAt" -> "e.created_at";
            case "status" -> "e.status";
            case "department.name" -> "d.name";
            case "department.code" -> "d.code";
            default -> "e." + property;
        };
    }

    private void setParameters(Query query, Map<String, Object> parameters) {
        parameters.forEach(query::setParameter);
    }
}