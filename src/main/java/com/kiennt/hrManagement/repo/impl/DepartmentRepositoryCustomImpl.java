package com.kiennt.hrManagement.repo.impl;

import com.kiennt.hrManagement.dto.response.DepartmentResponse;
import com.kiennt.hrManagement.repo.DepartmentRepositoryCustom;
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
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DepartmentRepositoryCustomImpl implements DepartmentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private final DepartmentResultTransformer resultTransformer;

    @Override
    public Page<DepartmentResponse> searchDepartmentsDynamic(String search, Pageable pageable) {
        log.info("Searching departments with dynamic SQL - search: {}", search);

        StringBuilder baseSql = new StringBuilder();
        baseSql.append("FROM departments d ");
        baseSql.append("LEFT JOIN employees e ON d.id = e.department_id AND e.status = 1 ");
        baseSql.append("WHERE d.status = 1 ");

        String selectSql = """
                SELECT 
                    d.id as id,
                    d.code as code,
                    d.name as name,
                    d.description as description,
                    d.status as status,
                    d.created_at as created_at,
                    d.created_by as created_by,
                    COUNT(e.id) as employee_count
                """;

        String countSql = "SELECT COUNT(DISTINCT d.id) ";

        StringBuilder condition = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();

        // Filter by search keyword
        if (StringUtils.hasText(search)) {
            condition.append(" AND (LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%')) ");
            condition.append(" OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))) ");
            parameters.put("search", search);
        }

        // Group by
        String groupBy = " GROUP BY d.id, d.code, d.name, d.description, d.status, d.created_at, d.created_by ";

        // Build order by
        String orderBy = buildOrderBy(pageable);

        String finalSelectSql = selectSql + baseSql + condition + groupBy + orderBy;
        String finalCountSql = countSql + baseSql + condition;

        log.debug("Department Search Query: {}", finalSelectSql);
        log.debug("Parameters: {}", parameters);

        // Create query for data (Tuple)
        Query query = entityManager.createNativeQuery(finalSelectSql, Tuple.class);
        setParameters(query, parameters);

        // Pagination
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Tuple> tuples = query.getResultList();
        List<DepartmentResponse> results = resultTransformer.mapTuplesToResponses(tuples);

        // Create query for count
        Query countQuery = entityManager.createNativeQuery(finalCountSql);
        setParameters(countQuery, parameters);

        Long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(results, pageable, total);
    }

    private String buildOrderBy(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return " ORDER BY d.created_at DESC";
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
        switch (property) {
            case "id":
                return "d.id";
            case "code":
                return "d.code";
            case "name":
                return "d.name";
            case "createdAt":
                return "d.created_at";
            case "employeeCount":
                return "employee_count";
            default:
                return "d." + property;
        }
    }

    private void setParameters(Query query, Map<String, Object> parameters) {
        parameters.forEach(query::setParameter);
    }
}