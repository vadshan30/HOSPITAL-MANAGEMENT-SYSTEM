package com.examly.springapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Admin;
import com.examly.springapp.repository.AdminRepository;

@Service
public class AdminServiceImpl implements AdminService{
	@Autowired
	private AdminRepository adminRepository;

	@Override
	public Admin createAdmin(Admin admin) {
		return adminRepository.save(admin);
	}

	@Override
	public Admin getAdminById(Long id) {
		return adminRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));
	}

	@Override
	public List<Admin> getAllAdmins() {
		return adminRepository.findAll();
	}

	@Override
	public Admin updateAdmin(Long id, Admin admin) {
		Admin existingAdmin = getAdminById(id);
		existingAdmin.setName(admin.getName());
		existingAdmin.setEmail(admin.getEmail());
		existingAdmin.setPassword(admin.getPassword());
		return adminRepository.save(existingAdmin);
	}

	@Override
	public void deleteAdmin(Long id) {
		Admin existingAdmin = getAdminById(id);
		adminRepository.delete(existingAdmin);
	}
}
