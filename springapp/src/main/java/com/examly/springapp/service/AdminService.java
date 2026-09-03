package com.examly.springapp.service;

import java.util.List;

import com.examly.springapp.model.Admin;

public interface AdminService {
	Admin createAdmin(Admin admin);
	Admin getAdminById(Long id);
	List<Admin> getAllAdmins();
	Admin updateAdmin(Long id, Admin admin);
	void deleteAdmin(Long id);
} 
