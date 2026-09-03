package com.examly.springapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.examly.springapp.model.Patient;
import com.examly.springapp.repository.PatientRepository;

@Service
public class PatientAccessService {

    @Autowired
    private PatientRepository patientRepository;

    public boolean isPatient(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_PATIENT".equals(authority.getAuthority()));
    }

    public boolean canAccess(Authentication authentication, Patient patient) {
        if (!isPatient(authentication)) {
            return true;
        }
        if (authentication == null || authentication.getName() == null) {
            return false;
        }
        return patient != null && patient.getEmail() != null
                && patient.getEmail().equals(authentication.getName());
    }

    public Patient getAuthenticatedPatient(Authentication authentication) {
        if (!isPatient(authentication)) {
            return null;
        }
        return patientRepository.findByEmail(authentication.getName()).orElse(null);
    }
}