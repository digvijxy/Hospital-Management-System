package com.hospital.management.service;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.User;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.UserRepository;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Locale;
import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Doctor getDoctorById(int id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));
    }
    @Override
    public void deleteDoctorById(int id) {
        doctorRepository.deleteById(id);
    }
    @Override
    public void updateDoctor(int id, Doctor updatedDoctor) {
        Doctor existingDoctor = getDoctorById(id);
        existingDoctor.setName(updatedDoctor.getName());
        existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        existingDoctor.setContactNumber(updatedDoctor.getContactNumber());
        existingDoctor.setAvailabilitySchedule(updatedDoctor.getAvailabilitySchedule());
        doctorRepository.save(existingDoctor);
    }
    @Override
    public void saveDoctorWithUser(Doctor doctor) {
        // 1. Create a new User for the Doctor
        User user = new User();
        user.setUserName(doctor.getName().toLowerCase().replaceAll("\\s+", "") + doctor.getContactNumber());
        user.setPassword("{noop}doctor@123"); // Use BCrypt encoder in production
        user.setRole(User.Role.DOCTOR);

        // 2. Save User first to generate user_id
        User savedUser = userRepository.save(user);

        // 3. Set user in Doctor entity
        doctor.setUser(savedUser);

        // 4. Save Doctor
        doctorRepository.save(doctor);
    }
    @Override
    public List<String> getAvailabilitySlots(int doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null || doctor.getAvailabilitySchedule() == null) {
            return Collections.emptyList();
        }

        // Assuming newline or comma separated values
        return Arrays.stream(doctor.getAvailabilitySchedule().split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }
    @Override
    public void saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    @Override
    public boolean isDoctorAvailableToday(Doctor doctor) {
        String schedule = doctor.getAvailabilitySchedule();
        if (schedule == null || schedule.trim().isEmpty()) return false;

        String today = LocalDate.now().getDayOfWeek().name(); // e.g., MONDAY
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        return Arrays.stream(schedule.split("\\r?\\n"))
                .map(String::toUpperCase)
                .anyMatch(line -> {
                    try {
                        if (!line.contains(":") || !line.contains("-")) return false;

                        // Example: "MONDAY-FRIDAY: 09:00 AM - 05:00 PM"
                        String[] dayAndTime = line.split(":", 2);
                        if (dayAndTime.length != 2) return false;

                        String daysPart = dayAndTime[0].trim();         // "MONDAY-FRIDAY"
                        String timeRange = dayAndTime[1].trim();        // "09:00 AM - 05:00 PM"

                        String[] timeSplit = timeRange.split("-");
                        if (timeSplit.length != 2) return false;

                        LocalTime start = LocalTime.parse(timeSplit[0].trim(), formatter);
                        LocalTime end = LocalTime.parse(timeSplit[1].trim(), formatter);

                        if (daysPart.contains("-")) {
                            String[] dayRange = daysPart.split("-");
                            if (dayRange.length != 2) return false;

                            String startDay = dayRange[0].trim();
                            String endDay = dayRange[1].trim();

                            if (isTodayInRange(startDay, endDay, today)) {
                                return !now.isBefore(start) && !now.isAfter(end);
                            }

                        } else {
                            return daysPart.equalsIgnoreCase(today)
                                    && !now.isBefore(start) && !now.isAfter(end);
                        }

                    } catch (Exception e) {
                        return false;
                    }
                    return false;
                });
    }


    private boolean isTodayInRange(String start, String end, String today) {
        List<String> days = Arrays.asList(
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        );
        int startIndex = days.indexOf(start);
        int endIndex = days.indexOf(end);
        int todayIndex = days.indexOf(today);

        System.out.println("Start index: " + startIndex);
        System.out.println("End index: " + endIndex);
        System.out.println("Today index: " + todayIndex);

        if (startIndex == -1 || endIndex == -1 || todayIndex == -1) return false;

        return todayIndex >= startIndex && todayIndex <= endIndex;
    }


    @Override
    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Optional<Object> getByContactNumber(String contactNumber){
        return  doctorRepository.findByContactNumber(contactNumber);
    }
    @Override
    public Doctor getDoctorByUser(User user){
        return doctorRepository.findByUser(user);
    }


}
