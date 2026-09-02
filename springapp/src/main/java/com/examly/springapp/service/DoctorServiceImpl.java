package com.examly.springapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.examly.springapp.model.Doctor;
import com.examly.springapp.repository.DoctorRepository;

@Service

public class DoctorServiceImpl implements DoctorService{
    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Doctor addDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }


    @Override
    public Doctor getDoctorid(Long id) {
        Optional<Doctor> existingDoctor=doctorRepository.findById(id);
        if(existingDoctor.isPresent())
        {
           return existingDoctor.get();
           
        }
        return null;
       
    }

    @Override
    public Doctor updateDoctorById(Long id, Doctor doctor) {
        Optional<Doctor> existingDoctor=doctorRepository.findById(id);
        if(existingDoctor.isPresent())
        {
            Doctor newDoctor=existingDoctor.get();
            newDoctor.setName(doctor.getName());
            newDoctor.setEmail(doctor.getEmail());
            newDoctor.setSpecialization(doctor.getSpecialization());
            newDoctor.setPhone(doctor.getPhone());
            newDoctor.setRoomNumber(doctor.getRoomNumber());
            return doctorRepository.save(newDoctor);

        }
        return null;

        
    }
    @Override
    public Doctor deleteDoctorById(Long id)
    {
        Optional<Doctor> existingDoctor=doctorRepository.findById(id);
        if(existingDoctor.isPresent())
        {
            Doctor doctor=existingDoctor.get();
            doctorRepository.deleteById(id);
            return doctor;
        }
        return null;


    }

    @Override
    public Page<Doctor> getDoctors(int page, int size) {
      Pageable pageable=PageRequest.of(page, size);
      return doctorRepository.findAll(pageable);    
    }

    
}
