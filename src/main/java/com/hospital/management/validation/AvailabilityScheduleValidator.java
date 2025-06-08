package com.hospital.management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AvailabilityScheduleValidator implements ConstraintValidator<ValidSchedule, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    @Override
    public boolean isValid(String schedule, ConstraintValidatorContext context) {
        if (schedule == null || schedule.trim().isEmpty()) return true;

        try {
            if (!schedule.contains(":") || !schedule.contains("-")) return false;

            String timePart = schedule.split(":", 2)[1].trim();
            String[] times = timePart.split("-");
            if (times.length != 2) return false;

            LocalTime start = LocalTime.parse(times[0].trim(), FORMATTER);
            LocalTime end = LocalTime.parse(times[1].trim(), FORMATTER);

            return end.isAfter(start);
        } catch (Exception e) {
            return false;
        }
    }
}
