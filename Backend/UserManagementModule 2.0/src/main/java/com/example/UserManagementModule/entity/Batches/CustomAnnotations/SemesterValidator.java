package com.example.UserManagementModule.entity.Batches.CustomAnnotations;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SemesterValidator implements ConstraintValidator<ValidSemester, Integer> {

    private int min;
    private int max;

    @Override
    public void initialize(ValidSemester constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer semester, ConstraintValidatorContext context) {
        if (semester == null) {
            return true; // You can decide if null is valid or not based on your requirements
        }
        return semester >= min && semester <= max;
    }
}

