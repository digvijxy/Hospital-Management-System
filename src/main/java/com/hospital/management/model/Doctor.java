package com.hospital.management.model;

import com.hospital.management.validation.ValidSchedule;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.hospital.management.validation.AvailabilityScheduleValidator;

@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private int doctorId;

    @NotBlank(message = "Doctor name is required")
    @Pattern(regexp = "^[A-Za-z. ]{2,}$", message = "Name must be at least 2 characters and contain only letters and spaces")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Specialization is required")
    @Column(name = "specialization", nullable = false)
    private String specialization;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "\\d{10}", message = "Contact number must be 10 digits")
    @Column(name = "contact_number", unique = true, nullable = false)
    private String contactNumber;

    @ValidSchedule
    @Column(name = "availability_schedule")
    private String availabilitySchedule;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    // Getters and Setters
    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAvailabilitySchedule() {
        return availabilitySchedule;
    }

    public void setAvailabilitySchedule(String availabilitySchedule) {
        this.availabilitySchedule = availabilitySchedule;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
