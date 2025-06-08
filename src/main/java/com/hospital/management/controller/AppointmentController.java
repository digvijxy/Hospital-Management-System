package com.hospital.management.controller;

import com.hospital.management.model.*;
import com.hospital.management.repository.*;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.DoctorService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    private void setModelAttributes(Model model, Patient patient, Doctor doctor, int doctorId) {
        model.addAttribute("patientName", patient.getName());
        model.addAttribute("doctorName", doctor.getName());
        model.addAttribute("specialization", doctor.getSpecialization());
        model.addAttribute("availability", doctor.getAvailabilitySchedule());
        model.addAttribute("id", doctorId);
    }

    @GetMapping("/patient/appointment/{id}")
    public String showForm(@PathVariable("id") int doctorId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow();
        //System.out.println("Hello"+user.getUserName());
        Patient patient = patientRepository.findByUser(user).orElseThrow();
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();

        model.addAttribute("patientName", patient.getName());
        model.addAttribute("doctorName", doctor.getName());
        model.addAttribute("specialization", doctor.getSpecialization());
        model.addAttribute("availability", doctor.getAvailabilitySchedule());
        model.addAttribute("id", doctorId);
        model.addAttribute("appointment", new Appointment());
        return "appointment-form";
    }


    @PostMapping("/schedule-appointment/{id}")
    public String scheduleAppointment(@PathVariable("id") int doctorId,
                                      @Valid @ModelAttribute("appointment") Appointment appointment,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow();
        Patient patient = patientRepository.findByUser(user).orElseThrow();
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();

        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> {
                System.out.println(" - " + error.getObjectName() + ": " + error.getDefaultMessage());
            });
            setModelAttributes(model, patient, doctor, doctorId);
            return "appointment-form";
        }

        // ✅ Parse and validate appointment time against availability
        if (!isWithinDoctorAvailability(doctor.getAvailabilitySchedule(), appointment)) {
            model.addAttribute("error", "The doctor is not available at the selected date and time.");
            setModelAttributes(model, patient, doctor, doctorId);
            return "appointment-form";
        }

        // ✅ Check slot availability
        boolean slotAvailableForDoctor = appointmentRepository
                .findByDoctorAndAppointmentDateAndTimeSlot(doctorId,
                        appointment.getAppointmentDate(),
                        appointment.getTimeSlot())
                .isEmpty();

        if (!slotAvailableForDoctor) {
            model.addAttribute("error", "This time slot is already booked for this doctor.");
            setModelAttributes(model, patient, doctor, doctorId);
            return "appointment-form";
        }

        // ✅ Check if patient already booked
        boolean patientAlreadyBooked = !appointmentRepository
                .findByPatientAndAppointmentDateAndTimeSlot(patient,
                        appointment.getAppointmentDate(),
                        appointment.getTimeSlot())
                .isEmpty();

        if (patientAlreadyBooked) {
            model.addAttribute("error", "You already have another appointment at this time.");
            setModelAttributes(model, patient, doctor, doctorId);
            return "appointment-form";
        }

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointmentRepository.save(appointment);

        redirectAttributes.addFlashAttribute("appointment", appointment);
        redirectAttributes.addFlashAttribute("doctorId", doctorId);
        return "redirect:/appointment-recipt/";
    }



    @GetMapping("/appointment-recipt/")
    public String receipt(@ModelAttribute("appointment") Appointment appointment,
                          @ModelAttribute("doctorId") int doctorId,
                          Model model) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        model.addAttribute("appointment", appointment);
        model.addAttribute("doctorName", doctor.getName());
        model.addAttribute("specialization", doctor.getSpecialization());
        model.addAttribute("availability", doctor.getAvailabilitySchedule());
        return "appointment-recipt";
    }
    @GetMapping("/patient/viewAppointments")
    public String viewAppointments(Model model) {
        // Get logged-in patient from security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username).orElseThrow();
        Patient patient = patientRepository.findByUser(user).orElseThrow();

        List<Appointment> appointments = appointmentRepository.findByPatient_PatientId(patient.getPatientId());
        model.addAttribute("appointments", appointments);
        return "patient-appointment"; // your HTML page
    }
    private boolean isWithinDoctorAvailability(String availabilitySchedule, Appointment appointment) {
        if (availabilitySchedule == null || availabilitySchedule.isEmpty()) return false;

        DayOfWeek appointmentDay = appointment.getAppointmentDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .getDayOfWeek();

        String timeSlot = appointment.getTimeSlot(); // Format: "09:00 AM - 09:30 AM"
        String[] times = timeSlot.split("-");
        if (times.length != 2) return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        LocalTime appointmentStart = LocalTime.parse(times[0].trim(), formatter);
        LocalTime appointmentEnd = LocalTime.parse(times[1].trim(), formatter);

        for (String line : availabilitySchedule.split("\\r?\\n")) {
            if (!line.contains(":")) continue;

            String[] parts = line.split(":", 2);
            String daysPart = parts[0].trim().toUpperCase(); // e.g. MONDAY-FRIDAY
            String timePart = parts[1].trim();               // e.g. 09:00 AM - 05:00 PM

            if (!timePart.contains("-")) continue;
            String[] timeRange = timePart.split("-");
            LocalTime availableStart = LocalTime.parse(timeRange[0].trim(), formatter);
            LocalTime availableEnd = LocalTime.parse(timeRange[1].trim(), formatter);

            List<DayOfWeek> days = getDaysFromRange(daysPart);
            if (days.contains(appointmentDay)) {
                if (!appointmentStart.isBefore(availableStart) && !appointmentEnd.isAfter(availableEnd)) {
                    return true;
                }
            }
        }

        return false;
    }
    private List<DayOfWeek> getDaysFromRange(String range) {
        List<DayOfWeek> days = Arrays.asList(DayOfWeek.values());

        if (range.contains("-")) {
            String[] split = range.split("-");
            if (split.length == 2) {
                DayOfWeek start = DayOfWeek.valueOf(split[0].trim().toUpperCase());
                DayOfWeek end = DayOfWeek.valueOf(split[1].trim().toUpperCase());
                int startIndex = days.indexOf(start);
                int endIndex = days.indexOf(end);
                return days.subList(startIndex, endIndex + 1);
            }
        } else {
            try {
                return List.of(DayOfWeek.valueOf(range.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        return List.of();
    }


}
