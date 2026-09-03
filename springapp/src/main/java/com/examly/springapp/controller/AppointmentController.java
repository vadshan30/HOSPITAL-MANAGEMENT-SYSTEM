package com.examly.springapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.examly.springapp.model.Appointment;
import com.examly.springapp.model.Patient;
import com.examly.springapp.repository.PatientRepository;
import com.examly.springapp.service.AppointmentService;
import com.examly.springapp.service.PatientAccessService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientAccessService patientAccessService;

    @Autowired
    private PatientRepository patientRepository;
    
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody Appointment appointment) {
        Appointment savedAppointment = appointmentService.addAppointment(appointment);
        return new ResponseEntity<>(savedAppointment, HttpStatus.CREATED);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable String status,
                                                                      Authentication authentication) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
        if (patientAccessService.isPatient(authentication)) {
            appointments = appointments.stream()
                    .filter(appointment -> patientAccessService.canAccess(authentication, appointment.getPatient()))
                    .toList();
        }
        if (appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id, Authentication authentication) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (!patientAccessService.canAccess(authentication, appointment.getPatient())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(appointment);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @Valid @RequestBody Appointment appointment, Authentication authentication) {
        Appointment existingAppointment = appointmentService.getAppointmentById(id);
        Patient existingPatient = patientRepository.findById(existingAppointment.getPatient().getId()).orElse(null);
        if (!patientAccessService.canAccess(authentication, existingPatient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Appointment updatedAppointment = appointmentService.updateAppointmentById(id, appointment);
        return ResponseEntity.ok(updatedAppointment);
    }
}
