package com.hospital.management.controller;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.Patient;
import com.hospital.management.model.User;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.repository.UserRepository;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;



    @GetMapping("/register")
    public String showPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patientRegister"; // this should exactly match patientRegister.html
    }

    @PostMapping("/register")
    public String registerPatient(@Valid @ModelAttribute("patient") Patient patient,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "patientRegister";  // or "patient-form" if that's your template
        }

        if (patientService.isContactNumberDuplicate(patient.getContactNumber())) {
            result.rejectValue("contactNumber", "error.patient", "Contact number already exists");
            return "patientRegister";
        }
        String contact = patient.getContactNumber();
        String safeUsername = patient.getName() + "_" + contact.substring(contact.length() - 4);

        User user = new User();
        user.setUserName(safeUsername);
        user.setPassword("test");
        user.setRole(User.Role.PATIENT);
        userRepository.save(user);

        patient.setUser(user);
        patientService.savePatient(patient);

        redirectAttributes.addFlashAttribute("success", "Patient registered successfully.");
        return "redirect:/admin";
    }


    @GetMapping("/dashboard")
    public String showDoctorDashboard(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Doctor> doctors = (search != null && !search.isEmpty()) ?
                doctorService.searchDoctorsByName(search) :
                doctorService.getAllDoctors();
        model.addAttribute("search", search);
        model.addAttribute("doctors", doctors);
        return "patientDashboard";
    }

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        patientService.deletePatientById(id);
        redirectAttributes.addFlashAttribute("success", "Patient deleted successfully.");
        return "redirect:/patient/dashboard";
    }

    @GetMapping("/doctorDetails/{id}")
    public String showDoctorDetails(@PathVariable int id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        return "Onedoctordetails";
    }
}
