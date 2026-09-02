package com.examly.springapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Optional<Patient> existingPatient = patientRepository.findById(medicalRecord.getPatient().getId());
        if (existingPatient.isPresent()) {
            medicalRecord.setPatient(existingPatient.get());
        }
        return medicalRecordRepository.save(medicalRecord);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    @Override
    public MedicalRecord getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id).orElse(null);
    }

    @Override
    public MedicalRecord updateMedicalRecordById(Long id, MedicalRecord medicalRecord) {
        Optional<MedicalRecord> existingRecord = medicalRecordRepository.findById(id);
        if (existingRecord.isPresent()) {
            MedicalRecord newRecord = existingRecord.get();
            newRecord.setDiagnosis(medicalRecord.getDiagnosis());
            newRecord.setPrescription(medicalRecord.getPrescription());
            return medicalRecordRepository.save(newRecord);
        }
        return null;
    }
}
