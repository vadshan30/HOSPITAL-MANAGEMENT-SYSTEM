package com.examly.springapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.examly.springapp.model.Patient;
import com.examly.springapp.repository.PatientRepository;

@Service
public class PatientServiceImpl implements PatientService{
    @Autowired
   private PatientRepository patientRepository;

    @Override
    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);

        }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getPatientId(Long id) {
        Optional<Patient> existingPatient=patientRepository.findById(id);
        if(existingPatient.isPresent())
        {
            return existingPatient.get();
        }
        return null;
       }

    @Override
    public Patient updatePatientById(Long id,Patient patient) {
        Optional<Patient> updatePatient=patientRepository.findById(id);
        if(updatePatient.isPresent())
        {
           Patient newpatient=updatePatient.get();
           newpatient.setName(patient.getName());
           newpatient.setEmail(patient.getEmail());
           newpatient.setPhone(patient.getPhone());
           newpatient.setAddress(patient.getAddress());
           newpatient.setAge(patient.getAge());

           return patientRepository.save(newpatient);
         }
         return null;
        }

    @Override
    public Patient deletePatientById(Long id) {
        Optional<Patient> existingPatient = patientRepository.findById(id);
        if (existingPatient.isPresent()) {
            Patient patient = existingPatient.get();
            patientRepository.deleteById(id);
            return patient;
        }
        return null;
    }
    
    
    
}
