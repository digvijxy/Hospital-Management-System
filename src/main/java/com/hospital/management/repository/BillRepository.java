package com.hospital.management.repository;

import com.hospital.management.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    @Query("SELECT b FROM Bill b ORDER BY b.billId DESC")
    List<Bill> getAllBills();
    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b")
    Double getTotalBillingAmount();
}
