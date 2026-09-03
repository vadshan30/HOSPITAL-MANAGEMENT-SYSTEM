package com.examly.springapp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ApiValidationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void invalidPatientAndDoctorBodiesReturnBadRequest() throws Exception {
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Doctor\",\"email\":\"not-an-email\",\"specialization\":\"Cardiology\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void invalidAppointmentAndMedicalRecordBodiesReturnBadRequest() throws Exception {
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"BOOKED\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnosis\":\"\",\"prescription\":\"Rest\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidPathAndMissingRelationshipReturnExpectedErrors() throws Exception {
        mockMvc.perform(get("/patients/not-a-number"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/doctors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());

        String appointment = """
                {
                  "patient": { "id": 999999 },
                  "doctor": { "id": 999999 },
                  "appointmentTime": "2026-01-10T09:00:00",
                  "status": "BOOKED"
                }
                """;
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appointment))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnosis\":\"Flu\",\"prescription\":\"Rest\"}"))
                .andExpect(status().isNotFound());
    }

                @Test
                void missingPatientForMedicalRecordLookupReturnsNotFound() throws Exception {
                                mockMvc.perform(get("/medicalrecords/patient/{patientId}", Long.MAX_VALUE))
                                                                .andExpect(status().isNotFound())
                                                                .andExpect(jsonPath("$.status").value(404));
                }

                @Test
                void relationshipWithoutIdOnUpdateReturnsNotFound() throws Exception {
                                mockMvc.perform(put("/appointments/{id}", Long.MAX_VALUE)
                                                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                                                .content("""
                                                                                                                                {
                                                                                                                                        "patient": {},
                                                                                                                                        "doctor": {},
                                                                                                                                        "appointmentTime": "2026-01-10T09:00:00",
                                                                                                                                        "status": "BOOKED"
                                                                                                                                }
                                                                                                                                """))
                                                                .andExpect(status().isNotFound());

                                mockMvc.perform(put("/medicalrecords/{id}", Long.MAX_VALUE)
                                                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                                                .content("""
                                                                                                                                {
                                                                                                                                        "diagnosis": "Flu",
                                                                                                                                        "prescription": "Rest",
                                                                                                                                        "patient": {}
                                                                                                                                }
                                                                                                                                """))
                                                                .andExpect(status().isNotFound());
                }

    @Test
    void successfulCreateUpdateAndDeleteReturnExpectedStatuses() throws Exception {
        String patient = """
                {"name":"Validation Patient","email":"validation@example.com","phone":"123","address":"Street","age":30}
                """;
        MvcResult createdPatient = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patient))
                .andExpect(status().isCreated())
                .andReturn();
        long patientId = idFrom(createdPatient);

        mockMvc.perform(put("/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patient.replace("Validation Patient", "Updated Validation Patient")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Validation Patient"));

        mockMvc.perform(delete("/patients/{id}", patientId))
                .andExpect(status().isNoContent());
    }

    private long idFrom(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
