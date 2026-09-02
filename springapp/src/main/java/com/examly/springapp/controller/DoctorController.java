package com.examly.springapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examly.springapp.model.Doctor;
import com.examly.springapp.service.DoctorService;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    @PostMapping()
    public  ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor)
    {
        Doctor savedDoctor=doctorService.addDoctor(doctor);
        return new ResponseEntity<>(savedDoctor,HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<Doctor>> getAllDoctors()
    {
        List<Doctor> isDoctor=doctorService.getAllDoctors();
        if(isDoctor.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(isDoctor,HttpStatus.OK);

        
    }
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id)
    {
        Doctor doctor = doctorService.getDoctorid(id);
        if (doctor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doctor,HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctorById(@PathVariable Long id,
    @RequestBody Doctor doctor)
    {
        Doctor updateDoctor=doctorService.updateDoctorById(id, doctor);

        if(updateDoctor==null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updateDoctor,HttpStatus.OK);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorById(@PathVariable Long id)
    {
        Doctor deletedDoctor = doctorService.deleteDoctorById(id);
        if (deletedDoctor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/page/{page}/{size}")

    public Page<Doctor> getDoctorsPage(
        @PathVariable int page, @PathVariable int size)
    {

        return doctorService.getDoctors(page, size);
    }


    
}
