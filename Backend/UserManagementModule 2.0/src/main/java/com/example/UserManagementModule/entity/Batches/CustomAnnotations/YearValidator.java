package com.example.UserManagementModule.entity.Batches.CustomAnnotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class YearValidator implements ConstraintValidator<ValidYear, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && (value == 1 || value == 2 || value == 3 || value == 4);
    }
}

