package com.hospital.management.model;

import io.micrometer.core.annotation.Counted;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_Id")
    private int billId;

    @Column(name="patient_id")
    private int patientId;

    @Column(name="totalAmount")
    private double totalAmount;

    public enum paymentStatus {
        PAID,UNPAID
    };

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentStatus")
    private Bill.paymentStatus paymentStatus;

    @Column(name="bill_date")
    @Temporal(TemporalType.DATE)
    private Date billDate;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", insertable = false,updatable = false)
    private Patient patient;

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus.name();
    }

    public void setPaymentStatus(paymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", patientId=" + patientId +
                ", totalAmount=" + totalAmount +
                ", paymentStatus=" + paymentStatus +
                ", billDate=" + billDate +
                ", patient=" + patient +
                '}';
    }
}

