package com.examly.springapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.examly.springapp.model.MedicalRecord;
import com.examly.springapp.service.MedicalRecordService;

@RestController
@RequestMapping("/medicalrecords")
public class MedicalRecordController {
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @PostMapping
    public ResponseEntity<MedicalRecord> createMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {
        MedicalRecord savedMedicalRecord = medicalRecordService.addMedicalRecord(medicalRecord);
        return new ResponseEntity<>(savedMedicalRecord, HttpStatus.CREATED);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getMedicalRecordsByPatient(@PathVariable Long patientId) {
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByPatientId(patientId);
        if (records.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No medical records found");
        }
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecordById(@PathVariable Long id) {
        MedicalRecord record = medicalRecordService.getMedicalRecordById(id);
        return ResponseEntity.ok(record);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable Long id, @Valid @RequestBody MedicalRecord medicalRecord) {
        MedicalRecord updatedRecord = medicalRecordService.updateMedicalRecordById(id, medicalRecord);
        return ResponseEntity.ok(updatedRecord);
    }
}
