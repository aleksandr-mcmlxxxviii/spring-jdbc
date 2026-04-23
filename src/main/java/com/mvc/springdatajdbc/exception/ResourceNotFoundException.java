package com.mvc.springdatajdbc.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Long id) {
        super(String.format("книга с id %d не найдена", id));
    }
}
