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

import com.examly.springapp.model.Patient;
import com.examly.springapp.repository.AppointmentRepository;
import com.examly.springapp.repository.MedicalRecordRepository;
import com.examly.springapp.repository.PatientRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MedicalRecordApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private long patientId;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        medicalRecordRepository.deleteAll();
        patientRepository.deleteAll();

        Patient patient = patientRepository.save(new Patient("Record Patient", "rec@example.com", "333", "Record St", 45));
        patientId = patient.getId();
    }

    // ---- Create ----

    @Test
    void createMedicalRecord_ValidBody_Returns201WithFields() throws Exception {
        mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recordJson("Flu", "Rest")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.diagnosis").value("Flu"))
                .andExpect(jsonPath("$.prescription").value("Rest"));
    }

    // ---- Get by ID ----

    @Test
    void getMedicalRecordById_Existing_Returns200() throws Exception {
        long id = createRecord("Diabetes", "Insulin");
        mockMvc.perform(get("/medicalrecords/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Diabetes"))
                .andExpect(jsonPath("$.prescription").value("Insulin"));
    }

    @Test
    void getMedicalRecordById_Missing_Returns404() throws Exception {
        mockMvc.perform(get("/medicalrecords/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ---- Get by patient ----

    @Test
    void getMedicalRecordsByPatient_ExistingPatientWithRecords_Returns200() throws Exception {
        createRecord("Hypertension", "Beta blockers");
        mockMvc.perform(get("/medicalrecords/patient/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diagnosis").value("Hypertension"))
                .andExpect(jsonPath("$[0].prescription").value("Beta blockers"));
    }

    @Test
    void getMedicalRecordsByPatient_MissingPatient_Returns404() throws Exception {
        mockMvc.perform(get("/medicalrecords/patient/{patientId}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getMedicalRecordsByPatient_PatientWithNoRecords_Returns204() throws Exception {
        Patient emptyPatient = patientRepository.save(new Patient("Empty Patient", "empty@example.com", "444", "Empty St", 20));
        mockMvc.perform(get("/medicalrecords/patient/{patientId}", emptyPatient.getId()))
                .andExpect(status().isNoContent());
    }

    // ---- Update ----

    @Test
    void updateMedicalRecord_Existing_Returns200WithUpdatedFields() throws Exception {
        long id = createRecord("Cold", "Paracetamol");
        mockMvc.perform(put("/medicalrecords/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recordJson("Severe Cold", "Antibiotics")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Severe Cold"))
                .andExpect(jsonPath("$.prescription").value("Antibiotics"));
    }

    @Test
    void updateMedicalRecord_Missing_Returns404() throws Exception {
        mockMvc.perform(put("/medicalrecords/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recordJson("X", "Y")))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMedicalRecord_PersistsToDatabase() throws Exception {
        long id = createRecord("Asthma", "Inhaler");
        mockMvc.perform(put("/medicalrecords/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recordJson("Chronic Asthma", "Steroid inhaler")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/medicalrecords/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Chronic Asthma"))
                .andExpect(jsonPath("$.prescription").value("Steroid inhaler"));
    }

    // ---- Invalid patient relationship ----

    @Test
    void createMedicalRecord_MissingPatientId_Returns404() throws Exception {
        mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnosis\":\"Flu\",\"prescription\":\"Rest\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMedicalRecord_InvalidPatientId_Returns404() throws Exception {
        mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnosis\":\"Flu\",\"prescription\":\"Rest\",\"patient\":{\"id\":999999}}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMedicalRecord_WithoutPatientId_Returns404() throws Exception {
        mockMvc.perform(put("/medicalrecords/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnosis\":\"Flu\",\"prescription\":\"Rest\",\"patient\":{}}"))
                .andExpect(status().isNotFound());
    }

    // ---- Validation ----

    @Test
    void createMedicalRecord_BlankDiagnosis_Returns400() throws Exception {
        mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnosis\":\"\",\"prescription\":\"Rest\",\"patient\":{\"id\":" + patientId + "}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createMedicalRecord_BlankPrescription_Returns400() throws Exception {
        mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnosis\":\"Flu\",\"prescription\":\"\",\"patient\":{\"id\":" + patientId + "}}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMedicalRecord_OversizedDiagnosis_Returns400() throws Exception {
        String longDiagnosis = "D".repeat(501);
        mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnosis\":\"" + longDiagnosis + "\",\"prescription\":\"Rest\",\"patient\":{\"id\":" + patientId + "}}"))
                .andExpect(status().isBadRequest());
    }

    // ---- Helper ----

    private String recordJson(String diagnosis, String prescription) {
        return "{\"diagnosis\":\"" + diagnosis + "\",\"prescription\":\"" + prescription + "\",\"patient\":{\"id\":" + patientId + "}}";
    }

    private long createRecord(String diagnosis, String prescription) throws Exception {
        MvcResult result = mockMvc.perform(post("/medicalrecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recordJson(diagnosis, prescription)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
