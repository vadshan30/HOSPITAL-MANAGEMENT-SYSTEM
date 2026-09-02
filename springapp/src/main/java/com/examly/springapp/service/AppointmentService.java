package com.examly.springapp.service;

import java.util.List;

import com.examly.springapp.model.Appointment;

public interface AppointmentService {
    Appointment addAppointment(Appointment appointment);
    List<Appointment> getAppointmentsByStatus(String status);
    Appointment getAppointmentById(Long id);
    Appointment updateAppointmentById(Long id, Appointment appointment);
}