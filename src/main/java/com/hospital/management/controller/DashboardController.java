package com.hospital.management.controller;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.User;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.UserRepository;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.BillingService;
import com.hospital.management.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class DashboardController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private BillingService billingService;

//    @GetMapping("/patientDashboard")
//    public String showPatientDashboard() {
//        return "patientDashboard"; // Loads patientDashboard.html from templates folder
//    }

    @GetMapping("/doctorDashboard")
    public String doctorDashboard(Model model, Principal principal) {
        User user = userRepository.findByUserName(principal.getName())
                .orElse(null);

        if (user == null) {
            return "redirect:/login?error=unauthorized";
        }

        Doctor doctor = doctorRepository.findByUser(user);
        boolean availableToday = doctorService.isDoctorAvailableToday(doctor);
        model.addAttribute("availableToday", availableToday);
        model.addAttribute("doctor", doctor);
        //System.out.println(appointmentService.getTodaysAppointmentsForDoctor(doctor.getDoctorId()));
        model.addAttribute("appointments", appointmentService.getTodaysAppointmentsForDoctor(doctor.getDoctorId()));
        model.addAttribute("availability", doctorService.getAvailabilitySlots(doctor.getDoctorId()));
        model.addAttribute("stats", appointmentService.getDoctorDashboardStats(doctor.getDoctorId()));

        long totalAppointments = appointmentService.getTotalAppointments();
        double totalBilling = billingService.getTotalBilling();

        model.addAttribute("noOfAppointments", totalAppointments);
        model.addAttribute("totalBilling", totalBilling);

        return "doctorDashboard";
    }
}