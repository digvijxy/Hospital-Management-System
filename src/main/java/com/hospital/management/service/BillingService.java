package com.hospital.management.service;

import com.hospital.management.model.Bill;
import com.hospital.management.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillingService {

    @Autowired
    private BillRepository billRepository;

    public Bill generateBill(Bill billData) {
        return billRepository.save(billData);
    }


    public Optional<Bill> getBillDetails(int billId) {
        return billRepository.findById(billId);
    }
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }


    public double getTotalBilling() {
        return billRepository.getTotalBillingAmount();
    }
    // 3️⃣ Process payment for a bill
    public Bill processPayment(int billId, Bill.paymentStatus paymentData) {
        Optional<Bill> billOpt = billRepository.findById(billId);
        if (billOpt.isPresent()) {
            Bill bill = billOpt.get();
            bill.setPaymentStatus(paymentData);
            return billRepository.save(bill);
        }
        return null;
    }
}


