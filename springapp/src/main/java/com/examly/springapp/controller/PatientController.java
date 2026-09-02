package com.examly.springapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.examly.springapp.model.Patient;
import com.examly.springapp.service.PatientService;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

@PostMapping
 public ResponseEntity<Patient> addPatient(@Valid @RequestBody Patient patient)
 {
    Patient savePatient=patientService.addPatient(patient);
    return new ResponseEntity<>(savePatient,HttpStatus.CREATED);

 }

@GetMapping
public ResponseEntity<List<Patient>> getAllPatients() {
    List<Patient> patients = patientService.getAllPatients();
    if (patients.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(patients, HttpStatus.OK);
}

@GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id)
    {
        Patient patient = patientService.getPatientId(id);
        if (patient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patient,HttpStatus.OK);
    }
@PutMapping("/{id}") 
public ResponseEntity<Patient> updatePatientById(@PathVariable Long id,@Valid @RequestBody Patient patient)
{

    Patient updatePatient=patientService.updatePatientById(id, patient);

    if(updatePatient==null)
    {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(updatePatient,HttpStatus.OK);

} 

@DeleteMapping("/{id}")
public ResponseEntity<Void> deletePatientById(@PathVariable Long id) {
    Patient deletedPatient = patientService.deletePatientById(id);
    if (deletedPatient == null) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
}
    
}
