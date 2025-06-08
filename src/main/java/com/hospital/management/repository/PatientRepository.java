package com.hospital.management.repository;

import com.hospital.management.model.Patient;
import com.hospital.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Integer> {
    Optional<Patient> findByUser(User user);

    Optional<Patient> findByContactNumber(String contactNumber);
}
