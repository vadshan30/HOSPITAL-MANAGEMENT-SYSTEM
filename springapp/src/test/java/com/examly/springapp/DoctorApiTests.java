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
import com.examly.springapp.repository.DoctorRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DoctorApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    // ---- Create ----

    @Test
    void createDoctor_ValidBody_Returns201WithFields() throws Exception {
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Dr. Smith","email":"smith@hospital.com","specialization":"Cardiology","phone":"555","roomNumber":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Dr. Smith"))
                .andExpect(jsonPath("$.email").value("smith@hospital.com"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"))
                .andExpect(jsonPath("$.roomNumber").value(10));
    }

    // ---- Get by ID ----

    @Test
    void getDoctorById_Existing_Returns200() throws Exception {
        long id = createDoctor("Dr. Jones", "jones@hospital.com", "Neurology");
        mockMvc.perform(get("/doctors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Jones"))
                .andExpect(jsonPath("$.specialization").value("Neurology"));
    }

    @Test
    void getDoctorById_Missing_Returns404() throws Exception {
        mockMvc.perform(get("/doctors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    // ---- Get all ----

    @Test
    void getAllDoctors_WhenEmpty_Returns204() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllDoctors_WhenPresent_Returns200WithList() throws Exception {
        createDoctor("Dr. A", "a@hospital.com", "Surgery");
        createDoctor("Dr. B", "b@hospital.com", "Pediatrics");
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ---- Update ----

    @Test
    void updateDoctor_Existing_Returns200WithUpdatedFields() throws Exception {
        long id = createDoctor("Dr. Old", "old@hospital.com", "Cardiology");
        mockMvc.perform(put("/doctors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Dr. New","email":"new@hospital.com","specialization":"Oncology","phone":"888","roomNumber":20}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. New"))
                .andExpect(jsonPath("$.specialization").value("Oncology"))
                .andExpect(jsonPath("$.roomNumber").value(20));
    }

    @Test
    void updateDoctor_Missing_Returns404() throws Exception {
        mockMvc.perform(put("/doctors/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"X","email":"x@hospital.com","specialization":"X","phone":"1","roomNumber":1}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDoctor_PersistsToDatabase() throws Exception {
        long id = createDoctor("Dr. Persist", "persist@hospital.com", "Radiology");
        mockMvc.perform(put("/doctors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Dr. Persisted","email":"persisted@hospital.com","specialization":"Dermatology","phone":"444","roomNumber":30}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/doctors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Persisted"))
                .andExpect(jsonPath("$.specialization").value("Dermatology"))
                .andExpect(jsonPath("$.roomNumber").value(30));
    }

    // ---- Delete ----

    @Test
    void deleteDoctor_Existing_Returns204() throws Exception {
        long id = createDoctor("Dr. Delete", "del@hospital.com", "Surgery");
        mockMvc.perform(delete("/doctors/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/doctors/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDoctor_Missing_Returns404() throws Exception {
        mockMvc.perform(delete("/doctors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    // ---- Pagination ----

    @Test
    void getDoctorsPage_Returns200WithPageStructure() throws Exception {
        createDoctor("Dr. Page1", "p1@hospital.com", "Cardiology");
        createDoctor("Dr. Page2", "p2@hospital.com", "Neurology");
        mockMvc.perform(get("/doctors/page/0/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));
    }

    @Test
    void getDoctorsPage_SecondPage_ReturnsCorrectPageSize() throws Exception {
        mockMvc.perform(get("/doctors/page/1/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.pageable.pageNumber").value(1));
    }

    // ---- Validation ----

    @Test
    void createDoctor_BlankName_Returns400() throws Exception {
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","email":"d@hospital.com","specialization":"Cardiology"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createDoctor_BlankSpecialization_Returns400() throws Exception {
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Dr. Valid","email":"v@hospital.com","specialization":" "}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDoctor_InvalidEmail_Returns400() throws Exception {
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Dr. Valid","email":"not-an-email","specialization":"Cardiology"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDoctor_NoBody_Returns400() throws Exception {
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDoctor_OversizedName_Returns400() throws Exception {
        String longName = "D".repeat(101);
        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + longName + "\",\"email\":\"v@hospital.com\",\"specialization\":\"Cardiology\"}"))
                .andExpect(status().isBadRequest());
    }

    // ---- Helper ----

    private long createDoctor(String name, String email, String specialization) throws Exception {
        MvcResult result = mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"specialization\":\"" + specialization + "\",\"phone\":\"123\",\"roomNumber\":1}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
