package com.examly.springapp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.examly.springapp.model.Doctor;
import com.examly.springapp.model.Patient;
import com.examly.springapp.repository.AppointmentRepository;
import com.examly.springapp.repository.DoctorRepository;
import com.examly.springapp.repository.MedicalRecordRepository;
import com.examly.springapp.repository.PatientRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AppointmentApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private long patientId;
    private long doctorId;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        medicalRecordRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();

        Patient patient = patientRepository.save(new Patient("Appt Patient", "appt@example.com", "111", "Street", 30));
        Doctor doctor = doctorRepository.save(new Doctor("Appt Doctor", "apptdoc@hospital.com", "Surgery", "222", 5));
        patientId = patient.getId();
        doctorId = doctor.getId();
    }

    // ---- Create ----

    @Test
    void createAppointment_ValidBody_Returns201WithFields() throws Exception {
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentJson("BOOKED", "Initial visit")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("BOOKED"))
                .andExpect(jsonPath("$.notes").value("Initial visit"));
    }

    // ---- Get by ID ----

    @Test
    void getAppointmentById_Existing_Returns200() throws Exception {
        long id = createAppointment("BOOKED", "Check-up");
        mockMvc.perform(get("/appointments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOOKED"))
                .andExpect(jsonPath("$.notes").value("Check-up"));
    }

    @Test
    void getAppointmentById_Missing_Returns404() throws Exception {
        mockMvc.perform(get("/appointments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ---- Update ----

    @Test
    void updateAppointment_Existing_Returns200WithUpdatedFields() throws Exception {
        long id = createAppointment("BOOKED", "First visit");
        mockMvc.perform(put("/appointments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentJson("COMPLETED", "Follow-up")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.notes").value("Follow-up"));
    }

    @Test
    void updateAppointment_Missing_Returns404() throws Exception {
        mockMvc.perform(put("/appointments/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentJson("BOOKED", "Notes")))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAppointment_PersistsToDatabase() throws Exception {
        long id = createAppointment("BOOKED", "Original");
        mockMvc.perform(put("/appointments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentJson("CANCELLED", "Cancelled by patient")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/appointments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.notes").value("Cancelled by patient"));
    }

    // ---- Get by status ----

    @Test
    void getAppointmentsByStatus_MatchingStatus_Returns200WithList() throws Exception {
        createAppointment("BOOKED", "Visit 1");
        mockMvc.perform(get("/appointments/status/BOOKED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("BOOKED"));
    }

    @Test
    void getAppointmentsByStatus_NoMatch_Returns204() throws Exception {
        mockMvc.perform(get("/appointments/status/CANCELLED"))
                .andExpect(status().isNoContent());
    }

    // ---- Invalid relationships ----

    @Test
    void createAppointment_InvalidPatientId_Returns404() throws Exception {
        String body = """
                {
                  "patient": {"id": 999999},
                  "doctor": {"id": %d},
                  "appointmentTime": "2026-06-01T10:00:00",
                  "status": "BOOKED"
                }
                """.formatted(doctorId);
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAppointment_InvalidDoctorId_Returns404() throws Exception {
        String body = """
                {
                  "patient": {"id": %d},
                  "doctor": {"id": 999999},
                  "appointmentTime": "2026-06-01T10:00:00",
                  "status": "BOOKED"
                }
                """.formatted(patientId);
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ---- Validation ----

    @Test
    void createAppointment_MissingAppointmentTime_Returns400() throws Exception {
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"BOOKED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createAppointment_InvalidDateFormat_Returns400() throws Exception {
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"appointmentTime\":\"not-a-date\",\"status\":\"BOOKED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void createAppointment_MissingStatus_Returns400() throws Exception {
        String body = """
                {
                  "patient": {"id": %d},
                  "doctor": {"id": %d},
                  "appointmentTime": "2026-06-01T10:00:00"
                }
                """.formatted(patientId, doctorId);
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAppointment_WithoutRelationshipIds_Returns404() throws Exception {
        mockMvc.perform(put("/appointments/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patient":{},"doctor":{},"appointmentTime":"2026-06-01T10:00:00","status":"BOOKED"}
                                """))
                .andExpect(status().isNotFound());
    }

    // ---- Helper ----

    private String appointmentJson(String status, String notes) {
        return """
                {
                  "patient": {"id": %d},
                  "doctor": {"id": %d},
                  "appointmentTime": "2026-06-01T10:00:00",
                  "status": "%s",
                  "notes": "%s"
                }
                """.formatted(patientId, doctorId, status, notes);
    }

    private long createAppointment(String status, String notes) throws Exception {
        MvcResult result = mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointmentJson(status, notes)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
