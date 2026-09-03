package com.examly.springapp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.examly.springapp.repository.AppointmentRepository;
import com.examly.springapp.repository.MedicalRecordRepository;
import com.examly.springapp.repository.PatientRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PatientApiTests {

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

    @BeforeEach
    void cleanUp() {
        appointmentRepository.deleteAll();
        medicalRecordRepository.deleteAll();
        patientRepository.deleteAll();
    }

    // ---- Create ----

    @Test
    void createPatient_ValidBody_Returns201WithFields() throws Exception {
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Alice","email":"alice@example.com","phone":"111","address":"1 St","age":25}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.phone").value("111"))
                .andExpect(jsonPath("$.address").value("1 St"))
                .andExpect(jsonPath("$.age").value(25));
    }

    // ---- Get by ID ----

    @Test
    void getPatientById_Existing_Returns200() throws Exception {
        long id = createPatient("Bob", "bob@example.com");
        mockMvc.perform(get("/patients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    void getPatientById_Missing_Returns404() throws Exception {
        mockMvc.perform(get("/patients/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPatientById_NonNumericId_Returns400() throws Exception {
        mockMvc.perform(get("/patients/not-a-number"))
                .andExpect(status().isBadRequest());
    }

    // ---- Get all ----

    @Test
    void getAllPatients_WhenEmpty_Returns204() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllPatients_WhenPresent_Returns200WithList() throws Exception {
        createPatient("Carol", "carol@example.com");
        createPatient("Dave", "dave@example.com");
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ---- Update ----

    @Test
    void updatePatient_Existing_Returns200WithUpdatedFields() throws Exception {
        long id = createPatient("Eve", "eve@example.com");
        mockMvc.perform(put("/patients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Eve Updated","email":"eve2@example.com","phone":"999","address":"2 Ave","age":30}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Eve Updated"))
                .andExpect(jsonPath("$.email").value("eve2@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void updatePatient_Missing_Returns404() throws Exception {
        mockMvc.perform(put("/patients/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"X","email":"x@example.com","phone":"1","address":"A","age":20}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePatient_PersistsToDatabase() throws Exception {
        long id = createPatient("Frank", "frank@example.com");
        mockMvc.perform(put("/patients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Frank Updated","email":"frank2@example.com","phone":"777","address":"3 Rd","age":40}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/patients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Frank Updated"))
                .andExpect(jsonPath("$.email").value("frank2@example.com"))
                .andExpect(jsonPath("$.age").value(40));
    }

    // ---- Delete ----

    @Test
    void deletePatient_Existing_Returns204() throws Exception {
        long id = createPatient("Grace", "grace@example.com");
        mockMvc.perform(delete("/patients/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/patients/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePatient_Missing_Returns404() throws Exception {
        mockMvc.perform(delete("/patients/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    // ---- Validation ----

    @Test
    void createPatient_BlankName_Returns400WithErrorStructure() throws Exception {
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","email":"valid@example.com","phone":"1","address":"A","age":20}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createPatient_InvalidEmail_Returns400WithMessage() throws Exception {
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Valid Name","email":"not-an-email","phone":"1","address":"A","age":20}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email: Email must be valid"));
    }

    @Test
    void createPatient_NullName_Returns400() throws Exception {
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"valid@example.com","phone":"1","address":"A","age":20}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPatient_OversizedName_Returns400() throws Exception {
        String longName = "A".repeat(101);
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + longName + "\",\"email\":\"v@example.com\",\"phone\":\"1\",\"address\":\"A\",\"age\":20}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPatient_MalformedJson_Returns400() throws Exception {
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not valid json"))
                .andExpect(status().isBadRequest());
    }

    // ---- Helper ----

    private long createPatient(String name, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"phone\":\"123\",\"address\":\"Street\",\"age\":30}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
