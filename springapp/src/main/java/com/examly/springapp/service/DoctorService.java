package com.examly.springapp.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.examly.springapp.model.Doctor;

public interface DoctorService{
    
    Doctor addDoctor(Doctor doctor);
    List<Doctor> getAllDoctors();
    Doctor getDoctorid(Long id);
    Doctor updateDoctorById(Long id,Doctor doctor);
    Doctor deleteDoctorById(Long id);
    Page<Doctor> getDoctors(int page,int size);


}
