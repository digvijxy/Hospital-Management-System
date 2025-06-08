package com.hospital.management.service;

import com.hospital.management.model.Patient;
import com.hospital.management.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients(){
        return patientRepository.findAll();
    }

    public Patient getPatientById(int id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + id));
    }

    public void deletePatientById(int id) {
        patientRepository.deleteById(id);
    }

    public boolean isContactNumberDuplicate(String contactNumber) {
        Optional<Patient> existing = patientRepository.findByContactNumber(contactNumber);
        return existing.isPresent();
    }

    public void savePatient(Patient patient) {
        patientRepository.save(patient);
    }
}
