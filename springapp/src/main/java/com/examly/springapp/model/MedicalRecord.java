package com.examly.springapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;
    @NotBlank(message = "Prescription is required")
    private String prescription;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    public MedicalRecord() {
    }
    
    public MedicalRecord(Long id, String diagnosis, String prescription, Patient patient) {
        this.id = id;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.patient = patient;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public String getPrescription() {
        return prescription;
    }
    
    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}