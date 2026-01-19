package com.kiennt.hrManagement.exception;

import lombok.Getter;

@Getter
public class DepartmentDuplicateException extends RuntimeException {
    private final String field;
    private final String value;

    public DepartmentDuplicateException(String field, String value) {
        super(String.format("%s '%s' already exists", field, value));
        this.field = field;
        this.value = value;
    }

}