package com.studentOrbit.generate_report_app.entity.Batches.CustomAnnotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SemesterValidator.class)  // Reference to the validator class
@Target({ ElementType.FIELD, ElementType.PARAMETER })  // Applicable to fields or method parameters
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSemester {
    String message() default "Semester must be between 1 and 8";  // Default error message

    Class<?>[] groups() default {};  // For grouping constraints

    Class<? extends Payload>[] payload() default {};  // Additional metadata

    int min() default 1;  // Default min value

    int max() default 8;  // Default max value
}

