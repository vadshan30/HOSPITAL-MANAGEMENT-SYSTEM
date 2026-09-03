package com.examly.springapp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.examly.springapp.model.AppUser;
import com.examly.springapp.model.UserRole;
import com.examly.springapp.repository.AdminRepository;
import com.examly.springapp.repository.AppUserRepository;
import com.examly.springapp.security.JwtUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("security-test")
class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
        adminRepository.deleteAll();
        appUserRepository.save(new AppUser("admin_user", encoder.encode("adminpass"), UserRole.ADMIN));
        appUserRepository.save(new AppUser("doctor_user", encoder.encode("doctorpass"), UserRole.DOCTOR));
        appUserRepository.save(new AppUser("patient_user", encoder.encode("patientpass"), UserRole.PATIENT));
    }

    // ---- JWT utility tests ----

    @Test
    void jwtGeneration_ShouldReturnNonNullToken() {
        String token = jwtUtils.generateToken("testuser", "ADMIN");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void jwtValidation_ValidToken_ShouldReturnTrue() {
        String token = jwtUtils.generateToken("testuser", "ADMIN");
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void jwtValidation_InvalidToken_ShouldReturnFalse() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }

    @Test
    void jwtExtractUsername_ShouldReturnCorrectUsername() {
        String token = jwtUtils.generateToken("testuser", "ADMIN");
        assertTrue(jwtUtils.extractUsername(token).equals("testuser"));
    }

    @Test
    void jwtExtractRole_ShouldReturnCorrectRole() {
        String token = jwtUtils.generateToken("testuser", "DOCTOR");
        assertTrue(jwtUtils.extractRole(token).equals("DOCTOR"));
    }

    // ---- AppUser password hashing tests ----

    @Test
    void appUserPassword_ShouldBeBCryptHashed() {
        AppUser user = appUserRepository.findByUsername("admin_user").orElseThrow();
        assertTrue(encoder.matches("adminpass", user.getPassword()),
                "Stored password should match BCrypt hash of original");
        assertFalse(user.getPassword().equals("adminpass"),
                "Stored password must not be plain text");
        assertTrue(user.getPassword().startsWith("$2"),
                "Stored password should be a BCrypt hash starting with $2");
    }

    // ---- Admin entity BCrypt password tests ----

    @Test
    void adminCreate_ShouldStoreBCryptHashedPassword() throws Exception {
        String adminJson = """
                {
                  "username": "BCrypt Admin",
                  "email": "bcrypt@example.com",
                  "password": "securepass123"
                }
                """;
        MvcResult result = mockMvc.perform(post("/api/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson)
                        .header("Authorization", "Bearer " + loginAndGetToken("admin_user", "adminpass")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        // Verify the stored password in DB is BCrypt hashed
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        long adminId = body.get("id").asLong();
        com.examly.springapp.model.Admin stored = adminRepository.findById(adminId).orElseThrow();
        assertTrue(encoder.matches("securepass123", stored.getPassword()),
                "Admin password in DB should be BCrypt hash of original");
        assertFalse(stored.getPassword().equals("securepass123"),
                "Admin password in DB must not be plain text");
        assertTrue(stored.getPassword().startsWith("$2"),
                "Admin password should be a BCrypt hash");
    }

    @Test
    void adminUpdate_ShouldStoreBCryptHashedPassword() throws Exception {
        // Create admin first
        String adminJson = """
                {
                  "username": "Update Admin",
                  "email": "update@example.com",
                  "password": "initialpass1"
                }
                """;
        String token = loginAndGetToken("admin_user", "adminpass");
        MvcResult created = mockMvc.perform(post("/api/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andReturn();
        long adminId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        // Update password
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/admin/{id}", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Update Admin",
                                  "email": "update@example.com",
                                  "password": "newpassword1"
                                }
                                """)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());

        com.examly.springapp.model.Admin stored = adminRepository.findById(adminId).orElseThrow();
        assertTrue(encoder.matches("newpassword1", stored.getPassword()),
                "Updated admin password in DB should be BCrypt hash");
        assertFalse(stored.getPassword().equals("newpassword1"),
                "Updated admin password must not be plain text");
    }

    // ---- Login tests ----

    @Test
    void login_ValidAdminCredentials_ShouldReturn200WithToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin_user\",\"password\":\"adminpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("admin_user"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();
        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void login_WithBCryptHashedPassword_ShouldSucceed() throws Exception {
        // Verify login works when the stored password is a BCrypt hash
        AppUser user = appUserRepository.findByUsername("admin_user").orElseThrow();
        assertTrue(user.getPassword().startsWith("$2"), "Pre-condition: password must be BCrypt hashed");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin_user\",\"password\":\"adminpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_InvalidCredentials_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin_user\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_NonExistentUser_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nobody\",\"password\":\"pass\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_MissingFields_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin_user\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void jwtExpiredToken_ShouldBeRejected() {
        byte[] secret = "test-hospital-management-jwt-secret-key-must-be-at-least-256-bits-long"
                .getBytes(StandardCharsets.UTF_8);
        String expiredToken = Jwts.builder()
                .setSubject("admin_user")
                .claim("role", "ADMIN")
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(secret), SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtils.validateToken(expiredToken));
    }

    @Test
    void login_DoctorCredentials_ShouldReturn200WithDoctorRole() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"doctor_user\",\"password\":\"doctorpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("DOCTOR"));
    }

    @Test
    void login_PatientCredentials_ShouldReturn200WithPatientRole() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"patient_user\",\"password\":\"patientpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("PATIENT"));
    }

    // ---- Protected endpoint tests ----

    @Test
    void protectedEndpoint_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_WithValidAdminToken_ShouldReturn2xx() throws Exception {
        String token = loginAndGetToken("admin_user", "adminpass");
        mockMvc.perform(get("/api/admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void protectedEndpoint_WithInvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_WithDoctorToken_ShouldReturn403() throws Exception {
        String token = loginAndGetToken("doctor_user", "doctorpass");
        mockMvc.perform(get("/api/admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_WithPatientToken_ShouldReturn403() throws Exception {
        String token = loginAndGetToken("patient_user", "patientpass");
        mockMvc.perform(get("/api/admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void doctorEndpoint_WithDoctorToken_ShouldReturn2xx() throws Exception {
        String token = loginAndGetToken("doctor_user", "doctorpass");
        mockMvc.perform(get("/doctors")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void patientEndpoint_WithPatientToken_ShouldReturn2xx() throws Exception {
        String token = loginAndGetToken("patient_user", "patientpass");
        mockMvc.perform(get("/patients")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is2xxSuccessful());
    }

        @Test
        void patientRole_CannotCreateDoctor() throws Exception {
                String token = loginAndGetToken("patient_user", "patientpass");
                mockMvc.perform(post("/doctors")
                                                .header("Authorization", "Bearer " + token)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"name\":\"Dr. Blocked\",\"email\":\"blocked@example.com\",\"specialization\":\"Surgery\"}"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void doctorRole_CannotCreatePatient() throws Exception {
                String token = loginAndGetToken("doctor_user", "doctorpass");
                mockMvc.perform(post("/patients")
                                                .header("Authorization", "Bearer " + token)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"name\":\"Blocked Patient\",\"email\":\"blocked@example.com\"}"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void patientRole_CannotCreateMedicalRecord() throws Exception {
                String token = loginAndGetToken("patient_user", "patientpass");
                mockMvc.perform(post("/medicalrecords")
                                                .header("Authorization", "Bearer " + token)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"diagnosis\":\"Flu\",\"prescription\":\"Rest\"}"))
                                .andExpect(status().isForbidden());
        }

    private String loginAndGetToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }
}
