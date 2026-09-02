package com.examly.springapp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.examly.springapp.model.Doctor;
import com.examly.springapp.model.Patient;
import com.examly.springapp.repository.DoctorRepository;
import com.examly.springapp.repository.PatientRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class CrudPersistenceTests {

    private static final long MISSING_ID = Long.MAX_VALUE;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void patientUpdatePersistsAndMissingIdsReturnNotFound() throws Exception {
        Patient patient = patientRepository.save(new Patient(
                "Patient Before", "before@example.com", "111", "Before Street", 30));

        String updatedPatient = """
                {
                  "name": "Patient After",
                  "email": "after@example.com",
                  "phone": "222",
                  "address": "After Street",
                  "age": 31
                }
                """;

        mockMvc.perform(put("/patients/{id}", patient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPatient))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Patient After"));

        mockMvc.perform(get("/patients/{id}", patient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("after@example.com"))
                .andExpect(jsonPath("$.age").value(31));

        mockMvc.perform(get("/patients/{id}", MISSING_ID))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/patients/{id}", MISSING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPatient))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/patients/{id}", MISSING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void doctorUpdatePersistsAndMissingIdsReturnNotFound() throws Exception {
        Doctor doctor = doctorRepository.save(new Doctor(
                "Doctor Before", "before.doctor@example.com", "Cardiology", "333", 101));

        String updatedDoctor = """
                {
                  "name": "Doctor After",
                  "email": "after.doctor@example.com",
                  "phone": "444",
                  "specialization": "Neurology",
                  "roomNumber": 202
                }
                """;

        mockMvc.perform(put("/doctors/{id}", doctor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDoctor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Doctor After"));

        mockMvc.perform(get("/doctors/{id}", doctor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("after.doctor@example.com"))
                .andExpect(jsonPath("$.roomNumber").value(202));

        mockMvc.perform(get("/doctors/{id}", MISSING_ID))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/doctors/{id}", MISSING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDoctor))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/doctors/{id}", MISSING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void appointmentUpdatePersistsAndMissingIdReturnsNotFound() throws Exception {
        Patient patient = patientRepository.save(new Patient(
                "Appointment Patient", "appointment.patient@example.com", "555", "Street", 40));
        Doctor doctor = doctorRepository.save(new Doctor(
                "Appointment Doctor", "appointment.doctor@example.com", "Surgery", "666", 303));

        String appointment = """
                {
                  "patient": { "id": %d },
                  "doctor": { "id": %d },
                  "appointmentTime": "2026-01-10T09:00:00",
                  "status": "BOOKED",
                  "notes": "Initial visit"
                }
                """.formatted(patient.getId(), doctor.getId());

        MvcResult created = mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointment))
                .andExpect(status().isCreated())
                .andReturn();
        long appointmentId = idFrom(created);

        String updatedAppointment = appointment.replace("2026-01-10T09:00:00", "2026-01-10T10:00:00")
                .replace("BOOKED", "COMPLETED")
                .replace("Initial visit", "Follow-up");
        mockMvc.perform(put("/appointments/{id}", appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAppointment))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(get("/appointments/{id}", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Follow-up"));
        mockMvc.perform(get("/appointments/{id}", MISSING_ID))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/appointments/{id}", MISSING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAppointment))
                .andExpect(status().isNotFound());
    }

    @Test
    void medicalRecordUpdatePersistsAndMissingIdReturnsNotFound() throws Exception {
        Patient patient = patientRepository.save(new Patient(
                "Record Patient", "record.patient@example.com", "777", "Record Street", 50));

        String record = """
                {
                  "diagnosis": "Initial diagnosis",
                  "prescription": "Initial prescription",
                  "patient": { "id": %d }
                }
                """.formatted(patient.getId());

        MvcResult created = mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(record))
                .andExpect(status().isCreated())
                .andReturn();
        long recordId = idFrom(created);

        String updatedRecord = record.replace("Initial diagnosis", "Updated diagnosis")
                .replace("Initial prescription", "Updated prescription");
        mockMvc.perform(put("/medicalrecords/{id}", recordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedRecord))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Updated diagnosis"));

        mockMvc.perform(get("/medicalrecords/{id}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prescription").value("Updated prescription"));
        mockMvc.perform(get("/medicalrecords/{id}", MISSING_ID))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/medicalrecords/{id}", MISSING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedRecord))
                .andExpect(status().isNotFound());
    }

    private long idFrom(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
