package com.examly.springapp.service;

import java.util.List;

import com.examly.springapp.model.Patient;

public interface PatientService {
     Patient addPatient(Patient patient);
     List<Patient> getAllPatients();
     Patient getPatientId(Long id);
     Patient updatePatientById(Long id,Patient patient);
     Patient deletePatientById(Long id);




    
} 
