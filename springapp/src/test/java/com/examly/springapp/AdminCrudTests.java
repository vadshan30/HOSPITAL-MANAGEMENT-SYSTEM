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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AdminCrudTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void adminCrudPersistsAndListsAdmins() throws Exception {
        String admin = """
                {
                  "username": "System Admin",
                  "email": "admin@example.com",
                  "password": "initial-secret"
                }
                """;

        MvcResult created = mockMvc.perform(post("/api/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(admin))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("System Admin"))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andReturn();
        long adminId = idFrom(created);

        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(adminId));

        mockMvc.perform(get("/api/admin/{id}", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(put("/api/admin/{id}", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Admin",
                                  "email": "updated-admin@example.com",
                                  "password": "updated-secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Admin"));

        mockMvc.perform(get("/api/admin/{id}", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated-admin@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(delete("/api/admin/{id}", adminId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/admin/{id}", adminId))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidAdminRequestsReturnConsistentErrors() throws Exception {
        mockMvc.perform(post("/api/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"invalid\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        mockMvc.perform(get("/api/admin/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mockMvc.perform(put("/api/admin/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Admin",
                                  "email": "admin@example.com",
                                  "password": "valid-secret"
                                }
                                """))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/admin/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    private long idFrom(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
