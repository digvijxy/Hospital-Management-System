package com.hospital.management.controller;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.Patient;
import com.hospital.management.model.User;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.UserRepository;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "doctor-list";
    }

    @PostMapping("/doctor/register")
    public String registerDoctor(@Valid @ModelAttribute("doctor") Doctor doctor,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "doctorRegister";
        }

        String baseUsername = doctor.getName().replaceAll("\\s+", "").toLowerCase();
        String suffix = doctor.getContactNumber().substring(Math.max(0, doctor.getContactNumber().length() - 4));
        String generatedUsername = baseUsername + "_" + suffix;

        if (userRepository.findByUserName(generatedUsername).isPresent()) {
            result.rejectValue("name", "error.doctor", "Username already exists");
            return "doctorRegister";
        }
        if (doctorService.getByContactNumber(doctor.getContactNumber()).isPresent()) {
            result.rejectValue("contactNumber", "error.doctor", "Contact number already exists");
            return "doctorRegister";
        }

        User user = new User();
        user.setUserName(generatedUsername);
        user.setPassword("test"); // hash this
        user.setRole(User.Role.DOCTOR);
        userRepository.save(user);

        doctor.setUser(user);
        doctorService.saveDoctor(doctor);
        //redirectAttributes.addFlashAttribute("successMessage", "Doctor registered successfully.");
        return "success";
    }



    @GetMapping("/doctors/add")
    public String showAddDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "add-doctor";
    }

    @PostMapping("/doctors/save")
    public String saveDoctor(@ModelAttribute Doctor doctor) {
        doctorService.saveDoctor(doctor);
        return "redirect:/doctors";
    }

    @GetMapping("/doctors/{id}")
    public String getDoctorDetails(@PathVariable int id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        return "doctor-details";
    }

    @GetMapping("/doctors/edit/{id}")
    public String showEditDoctorForm(@PathVariable int id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        return "edit-doctor";
    }

    @PostMapping("/doctors/update/{id}")
    public String updateDoctor(@PathVariable int id, @ModelAttribute Doctor doctor) {
        doctorService.updateDoctor(id, doctor);
        return "redirect:/doctors";
    }

    @GetMapping("/doctor/delete/{id}")
    public String deleteDoctor(@PathVariable("id") int id) {
        doctorService.deleteDoctorById(id);
        return "redirect:/doctors";
    }

    @GetMapping("/doctor/add-availability")
    public String showAvailabilityForm(Model model, Principal principal) {
        User user = userRepository.findByUserName(principal.getName()).orElse(null);
        if (user == null) return "error";

        Doctor doctor = doctorService.getDoctorByUser(user);
        //System.out.println(doctor.getName());
        model.addAttribute("doctor", doctor);
        return "add-availability";
    }

    @PostMapping("/doctor/add-availability")
    public String saveAvailability(@Valid @ModelAttribute("doctor") Doctor doctor,
                                   BindingResult result,
                                   Principal principal,
                                   Model model) {
        if (result.hasErrors()) {
            System.out.println("Inside susi " + doctor.getDoctorId()+ doctor.getName());
            result.getAllErrors().forEach(error -> {
                System.out.println("Error: " + error.getDefaultMessage());
            });
            return "add-availability";
        }

        User user = userRepository.findByUserName(principal.getName()).orElse(null);
        if (user == null) return "error";

        Doctor existingDoctor = doctorService.getDoctorByUser(user);
        existingDoctor.setAvailabilitySchedule(doctor.getAvailabilitySchedule());

        doctorService.saveDoctor(existingDoctor);
        return "redirect:/doctorDashboard";
    }



    @GetMapping("/doctor/patients")
    public String getExpiredPatients(Model model, Principal principal) {
        User user = userRepository.findByUserName(principal.getName()).orElse(null);
        if (user == null) return "error";

        Doctor doctor = doctorRepository.findByUser(user);
        List<Patient> patients = appointmentService.getAllPatientsForDoctor(doctor.getDoctorId());
        model.addAttribute("patients", patients);

        return "patients-seen";
    }

    @GetMapping("/doctor/todays-appointments")
    public String getTodaysAppointments(Model model, Principal principal) {
        User user = userRepository.findByUserName(principal.getName()).orElse(null);
        if (user == null) return "error";

        Doctor doctor = doctorRepository.findByUser(user);
        model.addAttribute("appointments", appointmentService.getTodaysAppointmentsForDoctor(doctor.getDoctorId()));
        return "todays-appointments";
    }

    @GetMapping("/patient/{id}")
    public String showPatientDetails(@PathVariable int id, Model model) {
        Patient patient = patientService.getPatientById(id);
        if (patient == null) return "error";

        model.addAttribute("patient", patient);
        return "singlePatientDetails";
    }
}
