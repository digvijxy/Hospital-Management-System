package com.hospital.management.controller;

import com.hospital.management.repository.BillRepository;
import com.hospital.management.service.BillingService;
import com.hospital.management.model.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;


@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private BillRepository billRepository;


    @GetMapping("/generateBill")
    public String generateBill(
            @RequestParam int patientId,
            @RequestParam String name,
            @RequestParam String doctor,
            @RequestParam String date,
            @RequestParam String timeSlot,
            @RequestParam String status,
            Model model) {

        model.addAttribute("patientId", patientId);
        model.addAttribute("name", name);
        model.addAttribute("doctor", doctor);
        model.addAttribute("date", date);
        model.addAttribute("timeSlot", timeSlot);
        model.addAttribute("status", status);
        model.addAttribute("totalAmount", 300); // Fixed payment amount

        return "paymentProcessing"; // ✅ Redirect to payment processing page
    }


    @PostMapping("/confirmPayment")
    public String confirmPayment(@ModelAttribute Bill bill, Model model) {
        bill.setPaymentStatus(Bill.paymentStatus.UNPAID); // ✅ Ensure correct status
        bill.setTotalAmount(300); // ✅ Hardcoded payment amount
        bill.setBillDate(new Date());
        // ✅ Save bill in the database
        Bill savedBill = billingService.generateBill(bill);

        model.addAttribute("bill", savedBill);
        return "paymentSuccess"; // ✅ Redirect to payment success page
    }




    // 2️⃣ Get bill details by ID
    @GetMapping("/getBillDetails")
    public String getAllBills(Model model) {
        List<Bill> bills = billingService.getAllBills();  // ✅ Fetch all bills
        model.addAttribute("bills", bills);  // ✅ Pass them to Thymeleaf
        return "billDetails";  // ✅ Load full bill table
    }




    // ✅ Toggle Payment Status (PAID <-> UNPAID)
    @PostMapping("/updatePaymentStatus/{billId}")
    public String updatePaymentStatus(@PathVariable int billId, RedirectAttributes redirectAttributes) {
        Optional<Bill> billOpt = billRepository.findById(billId);

        if (billOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bill not found!");
            return "redirect:/billing/paymentUpdate"; // ✅ Redirect to message page
        }

        Bill bill = billOpt.get();

        try {
            Bill.paymentStatus currentStatus = Bill.paymentStatus.valueOf(bill.getPaymentStatus());
            bill.setPaymentStatus(currentStatus == Bill.paymentStatus.PAID ? Bill.paymentStatus.UNPAID : Bill.paymentStatus.PAID);
            billRepository.save(bill);

            redirectAttributes.addFlashAttribute("successMessage", "Payment status updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid payment status.");
        }

        return "redirect:/billing/paymentUpdate"; // ✅ Redirects to message page instead of raw response
    }


    @GetMapping("/paymentUpdate")
    public String paymentUpdatePage() {
        return "paymentUpdate"; // ✅ Loads the new payment update message page
    }


    // 3️⃣ Process payment for a bill
    @PostMapping("/processPayment/{billId}")
    public String processPayment(@PathVariable int billId, @RequestParam Bill.paymentStatus paymentData, Model model) {
        Bill updatedBill = billingService.processPayment(billId, paymentData);
        model.addAttribute("bill", updatedBill);
        return updatedBill != null ? "paymentSuccess" : "billNotFound";  // ✅ Handles payment process
    }


    public String generateHtmlFromBill(Bill bill) {
        return "<html><body><h1>Hospital Bill Receipt</h1>"
                + "<p>Patient ID: " + bill.getPatientId() + "</p>"
                + "<p>Total Amount: ₹" + bill.getTotalAmount() + "</p>"
                + "<p>Payment Status: " + bill.getPaymentStatus() + "</p>"
                + "</body></html>";
    }

}



