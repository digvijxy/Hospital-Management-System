package com.hospital.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private int patientId;

    @NotBlank(message = "Name is required")
    @Column(name = "name")
    private String name;

    @NotNull(message = "{NotNull.patient.dateOfBirth}")
    @PastOrPresent(message = "{PastOrPresent.patient.dateOfBirth}")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Column(name = "gender")
    private String gender;

    @NotBlank(message = "{NotBlank.patient.contactNumber}")
    @Pattern(regexp = "\\d{10}", message = "{Pattern.patient.contactNumber}")
    @Column(name = "contact_number", unique = true)
    private String contactNumber;

    @NotBlank(message = "Address is required")
    @Column(name = "address")
    private String address;

    @Column(name = "medical_history")
    private String medicalHistory;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and setters (same as yours)


public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
}

