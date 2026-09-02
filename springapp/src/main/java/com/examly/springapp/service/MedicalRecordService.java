package com.examly.springapp.service;

import java.util.List;

import com.examly.springapp.model.MedicalRecord;

public interface MedicalRecordService {
    MedicalRecord addMedicalRecord(MedicalRecord medicalRecord);
    List<MedicalRecord> getMedicalRecordsByPatientId(Long patientId);
    MedicalRecord getMedicalRecordById(Long id);
    MedicalRecord updateMedicalRecordById(Long id, MedicalRecord medicalRecord);
}