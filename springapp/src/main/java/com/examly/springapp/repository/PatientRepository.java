package com.examly.springapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examly.springapp.model.Patient;

@Repository

public interface PatientRepository extends JpaRepository<Patient,Long>{
	Optional<Patient> findByEmail(String email);
}
