package com.hospital.management.service;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.User;

import java.util.List;
import java.util.Optional;

public interface DoctorService {
    Doctor getDoctorById(int id);
    void updateDoctor(int id, Doctor doctor);
    void saveDoctor(Doctor doctor);
    List<Doctor> getAllDoctors();

    void saveDoctorWithUser(Doctor doctor);
    boolean isDoctorAvailableToday(Doctor doctor);

    List<String> getAvailabilitySlots(int doctorId);
    List<Doctor> searchDoctorsByName(String name);

    void deleteDoctorById(int id);

    Optional<Object> getByContactNumber(String contactNumber);

    Doctor getDoctorByUser(User user);
}
