package com.hospital.management.repository;

import com.hospital.management.model.Appointment;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment , Integer> {

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate AND a.timeSlot = :timeSlot")
    Optional<Appointment> findByDoctorAndAppointmentDateAndTimeSlot(@Param("doctorId") int doctorId, @Param("appointmentDate") Date appointmentDate, @Param("timeSlot") String timeSlot);
    
    List<Appointment> findByPatient_PatientId(int patientId);
    List<Appointment> findByDoctor_DoctorId(int doctorId);
    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, Date appointmentDate);
    Optional<Appointment> findByDoctorAndAppointmentDateAndTimeSlot(
            Doctor doctor, Date appointmentDate, String timeSlot
    );

    Optional<Appointment> findByPatientAndAppointmentDateAndTimeSlot(
            Patient patient, Date appointmentDate, String timeSlot
    );

    long countByAppointmentDate(Date today);

    long countByStatus(Appointment.Status status);

    long countByAppointmentDateAfter(java.sql.Date today);
}
