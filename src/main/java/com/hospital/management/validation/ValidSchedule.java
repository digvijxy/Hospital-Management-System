package com.hospital.management.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AvailabilityScheduleValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSchedule  {
    String message() default "Each schedule must use format 'Day-Day: HH:mm AM/PM - HH:mm AM/PM' and end time must be after start time.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}