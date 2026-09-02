package com.examly.springapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.MedicalRecord;
import com.examly.springapp.model.Patient;
import com.examly.springapp.repository.MedicalRecordRepository;
import com.examly.springapp.repository.PatientRepository;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private PatientRepository patientRepository;

    @Override
    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord.getPatient() == null || medicalRecord.getPatient().getId() == null) {
            throw new ResourceNotFoundException("Patient ID is required");
        }
        Patient patient = patientRepository.findById(medicalRecord.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with ID: " + medicalRecord.getPatient().getId()));
        medicalRecord.setPatient(patient);
        return medicalRecordRepository.save(medicalRecord);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    @Override
    public MedicalRecord getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medical record not found with ID: " + id));
    }

    @Override
    public MedicalRecord updateMedicalRecordById(Long id, MedicalRecord medicalRecord) {
        MedicalRecord existingRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medical record not found with ID: " + id));

        if (medicalRecord.getPatient() != null && medicalRecord.getPatient().getId() != null) {
            Patient patient = patientRepository.findById(medicalRecord.getPatient().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Patient not found with ID: " + medicalRecord.getPatient().getId()));
            existingRecord.setPatient(patient);
        }

        existingRecord.setDiagnosis(medicalRecord.getDiagnosis());
        existingRecord.setPrescription(medicalRecord.getPrescription());
        return medicalRecordRepository.save(existingRecord);
    }
}
