package com.repairbro.commons.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RepairBroException {

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(
                String.format("%s not found with identifier: %s", resourceType, identifier),
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND");
    }
}
