package com.studentOrbit.generate_report_app.entity.Batches.CustomAnnotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = YearValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidYear {

    String message() default "Year must be 1, 2, 3, or 4";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
