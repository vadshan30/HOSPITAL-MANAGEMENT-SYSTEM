package com.examly.springapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import jakarta.validation.Valid;

import com.examly.springapp.model.Admin;
import com.examly.springapp.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	@Autowired
	private AdminService adminService;

	@PostMapping
	public ResponseEntity<Admin> createAdmin(@Valid @RequestBody Admin admin) {
		return new ResponseEntity<>(adminService.createAdmin(admin), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<Admin>> getAllAdmins() {
		List<Admin> admins = adminService.getAllAdmins();
		if (admins.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(admins);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
		return ResponseEntity.ok(adminService.getAdminById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @Valid @RequestBody Admin admin) {
		return ResponseEntity.ok(adminService.updateAdmin(id, admin));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
		adminService.deleteAdmin(id);
		return ResponseEntity.noContent().build();
	}
}
