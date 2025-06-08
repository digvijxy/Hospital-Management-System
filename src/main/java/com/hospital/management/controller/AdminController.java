package com.hospital.management.controller;

import com.hospital.management.model.Appointment;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.Patient;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Controller
public class AdminController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository; // ✅ Add this

    @Autowired
    public AdminController(PatientService patientService,
                           DoctorService doctorService,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository; // ✅ Assign this
    }

    @GetMapping("/admin")
    public String adminPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("message", "Hello " + userDetails.getUsername() + ", you are logged in as Admin.");
        model.addAttribute("noOfPatients", patientRepository.count());
        model.addAttribute("noOfDoctors", doctorRepository.count());
        model.addAttribute("noOfAppointments", appointmentRepository.count());

        Date today = Date.valueOf(LocalDate.now());

        long todaysAppointments = appointmentRepository.countByAppointmentDate(today);
        long scheduledAppointments = appointmentRepository.countByAppointmentDateAfter(today);

        model.addAttribute("todaysAppointments", todaysAppointments);
        model.addAttribute("scheduledAppointments", scheduledAppointments);

        return "admin";
    }

    @GetMapping("/patientRegister")
    public String showPatientForm() {
        return "patientRegister";
    }

    @GetMapping("/doctorRegister")
    public String showDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctorRegister";
    }

    @GetMapping("/doctorDetails")
    public String showDoctorDetails(Model model) {
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return "doctorDetails";
    }

    @GetMapping("/patientDetails")
    public String showPatientDetails(Model model) {
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        return "patientDetails";
    }

    @GetMapping("/doctorDetails/{id}")
    public String showDoctorDetails(@PathVariable int id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        return "Onedoctordetails";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }

    @GetMapping("/allappointment")
    public String viewAllAppointments(Model model) {
        List<Appointment> appointments = appointmentRepository.findAll();
        model.addAttribute("appointments", appointments);
        return "allappointments";
    }

    @GetMapping("/admin/appointment-delete/{id}")
    public String deleteAppointment(@PathVariable int id, RedirectAttributes redirectAttributes) {
        appointmentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Appointment deleted successfully.");
        return "redirect:/allappointment";
    }

    @GetMapping("/admin/appointment-update/{id}")
    public String updateAppointment(@PathVariable int id, Model model) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow();
        model.addAttribute("appointment", appointment);
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("patients", patientRepository.findAll());
        return "appointment-update-form"; // Create this HTML file
    }

    @PostMapping("/admin/appointment-update/{id}")
    public String processUpdateAppointment(@PathVariable int id,
                                           @ModelAttribute("appointment") Appointment appointment) {
        appointment.setAppointment_id(id);
        appointmentRepository.save(appointment);
        return "redirect:/allappointment";
    }

}
