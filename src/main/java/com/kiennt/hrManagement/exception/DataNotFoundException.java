package com.kiennt.hrManagement.exception;

public class DataNotFoundException extends RuntimeException {
    private final String resource;
    private final Long id;

    public DataNotFoundException(String resource, Long id) {
        super(String.format("%s not found with id: %d", resource, id));
        this.resource = resource;
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public Long getId() {
        return id;
    }
}