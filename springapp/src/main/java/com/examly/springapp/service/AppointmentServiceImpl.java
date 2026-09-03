package com.examly.springapp.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Appointment;
import com.examly.springapp.model.Doctor;
import com.examly.springapp.model.Patient;
import com.examly.springapp.repository.AppointmentRepository;
import com.examly.springapp.repository.DoctorRepository;
import com.examly.springapp.repository.PatientRepository;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Appointment addAppointment(Appointment appointment) {
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            throw new ResourceNotFoundException("Patient ID is required");
        }
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            throw new ResourceNotFoundException("Doctor ID is required");
        }
        Patient patient = patientRepository.findById(appointment.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with ID: " + appointment.getPatient().getId()));
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with ID: " + appointment.getDoctor().getId()));
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID: " + id));
    }

    @Override
    public Appointment updateAppointmentById(Long id, Appointment appointment) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID: " + id));

        if (appointment.getPatient() != null) {
            if (appointment.getPatient().getId() == null) {
                throw new ResourceNotFoundException("Patient ID is required");
            }
            Patient patient = patientRepository.findById(appointment.getPatient().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Patient not found with ID: " + appointment.getPatient().getId()));
            existingAppointment.setPatient(patient);
        }
        if (appointment.getDoctor() != null) {
            if (appointment.getDoctor().getId() == null) {
                throw new ResourceNotFoundException("Doctor ID is required");
            }
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Doctor not found with ID: " + appointment.getDoctor().getId()));
            existingAppointment.setDoctor(doctor);
        }

        existingAppointment.setAppointmentTime(appointment.getAppointmentTime());
        existingAppointment.setStatus(appointment.getStatus());
        existingAppointment.setNotes(appointment.getNotes());
        return appointmentRepository.save(existingAppointment);
    }
}
