package com.platform.ads.exception;

public class InvalidFieldValueException extends BusinessException {
    private final String fieldName;
    private final String expectedValue;

    public InvalidFieldValueException(String fieldName, String expectedValue) {
        super(String.format("Invalid value for field '%s'. Expected: %s", fieldName, expectedValue));
        this.fieldName = fieldName;
        this.expectedValue = expectedValue;
    }

    public String getFieldName() { return fieldName; }
    public String getExpectedValue() { return expectedValue; }
}
