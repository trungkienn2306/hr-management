package com.kiennt.hrManagement.exception;

import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException {
    private final String field;
    private final String value;

    public DuplicateException(String field, String value) {
        super(String.format("%s '%s' already exists", field, value));
        this.field = field;
        this.value = value;
    }

}