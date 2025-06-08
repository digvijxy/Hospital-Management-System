package com.hospital.management.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointmentId", nullable = false, unique = true)
    private int appointment_id;


    @ManyToOne
    @JoinColumn(name = "patientId", nullable = false)
    private Patient patient;


    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NotNull(message = "Appointment date is required")
//    @FutureOrPresent(message = "Appointment date cannot be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;

    @NotBlank(message = "Time slot is required")
    @Pattern(regexp = "^[0-9]{2}:[0-9]{2} (AM|PM) - [0-9]{2}:[0-9]{2} (AM|PM)$",
            message = "Time slot must be in the format 'HH:MM AM/PM - HH:MM AM/PM'")
    private String timeSlot;

    @NotNull(message = "Appointment status is required")
    @Enumerated(EnumType.STRING)
    private Status status;


    public enum Status {
        CONFIRMED,
        CANCELLED
    }
    public Appointment(){

    }
    // Constructor
    public Appointment(int appointmentId, Patient patient, Doctor doctor, Date appointmentDate, String timeSlot, Status status) {
        this.appointment_id = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
        this.status = status;
    }

    public int getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointment_id=" + appointment_id +
                ", patient=" + patient +
                ", doctor=" + doctor +
                ", appointmentDate=" + appointmentDate +
                ", timeSlot='" + timeSlot + '\'' +
                ", status=" + status +
                '}';
    }
}