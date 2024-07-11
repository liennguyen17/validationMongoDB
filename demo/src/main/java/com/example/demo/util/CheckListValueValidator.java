package com.example.demo.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class CheckListValueValidator implements ConstraintValidator<CheckListValueAnnotation,String> {
    private List<String> listValue;

    @Override
    public void initialize(CheckListValueAnnotation constraintAnnotation) {
        listValue = Arrays.asList(constraintAnnotation.listValue());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true; // Consider null as valid. Use @NotNull to enforce non-null.
        }
        return listValue.contains(value);
    }

}
